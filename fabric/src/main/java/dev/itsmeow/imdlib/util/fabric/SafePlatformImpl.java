package dev.itsmeow.imdlib.util.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class SafePlatformImpl {

    public static boolean isModLoaded(String modid) {
        return "minecraft".equals(modid) || FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean isClientEnv() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isForge() {
        return false;
    }

    public static boolean isFabric() {
        return true;
    }

}
