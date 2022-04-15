package dev.itsmeow.imdlib.util.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class SafePlatformImpl {
    public static boolean isModLoaded(String modid) {
        return "minecraft".equals(modid) || ModList.get().isLoaded(modid);
    }

    public static boolean isClientEnv() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isFabric() {
        return false;
    }
}
