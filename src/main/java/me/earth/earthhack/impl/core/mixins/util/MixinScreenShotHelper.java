package me.earth.earthhack.impl.core.mixins.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.chat.clickevents.RunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.render.ScreenShotRunnable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ScreenShotHelper.class})
public abstract class MixinScreenShotHelper {
    private static final SettingCache<Boolean, BooleanSetting, Management> POOL = Caches.getSetting(Management.class, BooleanSetting.class, "Pooled-ScreenShots", false);
    @Shadow
    private static IntBuffer field_74293_b;
    @Shadow
    private static int[] field_74294_c;

    @Redirect(method={"saveScreenshot(Ljava/io/File;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/text/ITextComponent;"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/ScreenShotHelper;saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/text/ITextComponent;"))
    private static ITextComponent saveScreenshot(File gameDirectory, @Nullable String name, int width, int height, Framebuffer buffer) {
        if (POOL.getValue().booleanValue()) {
            try {
                return MixinScreenShotHelper.makeScreenShot(gameDirectory, name, width, height, buffer);
            }
            catch (IOException e) {
                e.printStackTrace();
                return new TextComponentTranslation("screenshot.failure", new Object[]{e.getMessage()});
            }
        }
        return ScreenShotHelper.saveScreenshot((File)gameDirectory, null, (int)width, (int)height, (Framebuffer)buffer);
    }

    private static ITextComponent makeScreenShot(File gameDirectory, @Nullable String name, int width, int height, Framebuffer buffer) throws IOException {
        if (OpenGlHelper.isFramebufferEnabled()) {
            width = buffer.framebufferTextureWidth;
            height = buffer.framebufferTextureHeight;
        }
        int i = width * height;
        if (field_74293_b == null || field_74293_b.capacity() < i) {
            field_74293_b = BufferUtils.createIntBuffer(i);
            field_74294_c = new int[i];
        }
        GlStateManager.glPixelStorei((int)3333, (int)1);
        GlStateManager.glPixelStorei((int)3317, (int)1);
        field_74293_b.clear();
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture((int)buffer.framebufferTexture);
            GlStateManager.glGetTexImage((int)3553, (int)0, (int)32993, (int)33639, (IntBuffer)field_74293_b);
        } else {
            GlStateManager.glReadPixels((int)0, (int)0, (int)width, (int)height, (int)32993, (int)33639, (IntBuffer)field_74293_b);
        }
        field_74293_b.get(field_74294_c);
        AtomicBoolean finished = new AtomicBoolean();
        AtomicReference<String> supplier = new AtomicReference<String>("Creating Screenshot...");
        AtomicReference<File> file = new AtomicReference<File>();
        Managers.THREAD.submit(new ScreenShotRunnable(supplier, file, finished, width, height, field_74294_c, gameDirectory, name));
        SuppliedComponent component = new SuppliedComponent(supplier::get);
        component.getStyle().setClickEvent(new RunnableClickEvent(() -> {
            File f = (File)file.get();
            if (f != null) {
                URI uri = new File(f.getAbsolutePath()).toURI();
                try {
                    FileUtil.openWebLink(uri);
                }
                catch (Throwable t) {
                    Throwable cause = t.getCause();
                    Earthhack.getLogger().error("Couldn't open link: {}", cause == null ? "<UNKNOWN>" : cause.getMessage());
                }
            }
        }));
        component.getStyle().setUnderlined(true);
        return component;
    }
}
