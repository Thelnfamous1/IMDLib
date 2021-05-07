package dev.itsmeow.betteranimalsplus;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IMDLib.MOD_ID)
public class ForgeMod {
    public ForgeMod() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(IMDLib.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        IMDLib.init();
    }
}
