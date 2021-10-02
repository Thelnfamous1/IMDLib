package dev.itsmeow.imdlib.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class IMDLibImpl {

    public static MinecraftServer getForgeServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

}
