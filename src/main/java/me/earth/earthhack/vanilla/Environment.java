package me.earth.earthhack.vanilla;

import java.io.IOException;
import me.earth.earthhack.impl.core.util.AsmUtil;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public enum Environment {
    VANILLA,
    SEARGE,
    MCP;

    private static Environment environment;
    private static boolean forge;

    public static Environment getEnvironment() {
        return environment;
    }

    public static boolean hasForge() {
        return forge;
    }

    public static void loadEnvironment() {
        Environment env = SEARGE;
        try {
            String fml = "net.minecraftforge.common.ForgeHooks";
            byte[] forgeBytes = Launch.classLoader.getClassBytes(fml);
            if (forgeBytes != null) {
                forge = true;
            } else {
                env = VANILLA;
            }
        }
        catch (IOException e) {
            env = VANILLA;
        }
        String world = "net.minecraft.world.World";
        byte[] bs = null;
        try {
            bs = Launch.classLoader.getClassBytes(world);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (bs != null) {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(bs);
            reader.accept(node, 0);
            if (AsmUtil.findField(node, "loadedEntityList") != null) {
                env = MCP;
            }
        }
        environment = env;
    }
}
