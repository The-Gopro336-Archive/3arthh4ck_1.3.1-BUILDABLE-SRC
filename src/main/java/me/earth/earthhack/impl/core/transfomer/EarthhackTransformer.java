package me.earth.earthhack.impl.core.transfomer;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.EarthhackPatcher;
import me.earth.earthhack.impl.core.transfomer.patch.patches.BlockPosPatch;
import me.earth.earthhack.impl.core.transfomer.patch.patches.EntityPatch;
import me.earth.earthhack.impl.core.transfomer.patch.patches.EnumFacingPatch;
import me.earth.earthhack.impl.core.transfomer.patch.patches.InventoryPlayerPatch;
import me.earth.earthhack.impl.core.transfomer.patch.patches.PlayerControllerMPPatch;
import me.earth.earthhack.impl.core.transfomer.patch.patches.Vec3iPatch;
import me.earth.earthhack.impl.core.util.MixinHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.MixinEnvironment;

@IFMLLoadingPlugin.SortingIndex(value=0x7FFFFFFF)
public class EarthhackTransformer
implements IClassTransformer {
    private boolean changingPriority = true;
    private int reentrancy;

    public EarthhackTransformer() {
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT).addTransformerExclusion(EarthhackTransformer.class.getName());
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT).addTransformerExclusion(EarthhackTransformer.class.getName());
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT).addTransformerExclusion(EarthhackTransformer.class.getName());
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT).addTransformerExclusion(EarthhackTransformer.class.getName());
        EarthhackPatcher patches = EarthhackPatcher.getInstance();
        patches.addPatch(new InventoryPlayerPatch());
        patches.addPatch(new PlayerControllerMPPatch());
        patches.addPatch(new Vec3iPatch());
        patches.addPatch(new BlockPosPatch());
        patches.addPatch(new EnumFacingPatch());
        patches.addPatch(new EntityPatch());
        this.loadReentrantClasses();
        Core.LOGGER.info("Transformer instantiated.");
    }

    @Override
    public byte[] transform(String name, String transformed, byte[] b) {
        ++this.reentrancy;
        if (this.reentrancy > 1) {
            Core.LOGGER.warn("Transformer is reentrant on class: " + name + " : " + transformed + ".");
        }
        if (this.changingPriority) {
            try {
                MixinHelper.getHelper().establishDominance();
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (transformed.equals("net.minecraft.client.entity.EntityPlayerSP")) {
            Core.LOGGER.info("Done changing MixinPriority.");
            this.changingPriority = false;
        }
        byte[] r = EarthhackPatcher.getInstance().transform(name, transformed, b);
        --this.reentrancy;
        return r;
    }

    private void loadReentrantClasses() {
        try {
            Class.forName("me.earth.earthhack.impl.core.util.AsmUtil");
            Class.forName("me.earth.earthhack.impl.util.misc.ReflectionUtil");
            String pack = "me.earth.earthhack.impl.core.transfomer.patch.";
            Class.forName(pack + "patches.BlockPosPatch$OffsetPatch");
            Class.forName(pack + "patches.BlockPosPatch$Direction");
            Class.forName(pack + "patches.BlockPosPatch$1");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
