package dev.itsmeow.imdlib.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.client.util.ModelReplacementHandler;

public class SafePlatform {

    @ExpectPlatform
    public static boolean isModLoaded(String modid) {
        throw new RuntimeException("Platform expected");
    }

    @ExpectPlatform
    public static boolean isClientEnv() {
        throw new RuntimeException("Platform expected");
    }

    public static boolean isServerEnv() {
        return !isClientEnv();
    }

    @ExpectPlatform
    public static boolean isForge() {
        throw new RuntimeException("Platform expected");
    }

    @ExpectPlatform
    public static boolean isFabric() {
        throw new RuntimeException("Platform expected");
    }

    public static String modId() {
        return ModelReplacementHandler.INSTANCE != null ? ModelReplacementHandler.INSTANCE.parent_modid : IMDLib.getRegistries().getModId();
    }
}
