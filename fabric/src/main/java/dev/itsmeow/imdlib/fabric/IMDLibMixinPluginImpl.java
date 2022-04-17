package dev.itsmeow.imdlib.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class IMDLibMixinPluginImpl {
    public static boolean shouldApplyMixinPlatform() {
        return FabricLoader.getInstance().isModLoaded("architectury");
    }
}
