package dev.itsmeow.imdlib;

import net.fabricmc.api.ModInitializer;

public class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        IMDLib.init();
    }
}
