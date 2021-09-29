package dev.itsmeow.imdlib;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadedValue;

public class IMDLib {

    private static LazyLoadedValue<Registries> REGISTRIES;
    private static MinecraftServer SERVER;

    public static Registries getRegistries() {
        if(REGISTRIES == null) {
            throw new RuntimeException("Registries have not been initialized yet! Call IMDLib.setRegistry(modid) or use IMDLib.entityHandler(modid)");
        }
        return REGISTRIES.get();
    }

    public static <T> Registry<T> getRegistry(ResourceKey<net.minecraft.core.Registry<T>> key) {
        return getRegistries().get(key);
    }

    public static void setRegistry(String modid) {
        REGISTRIES = new LazyLoadedValue<>(() -> Registries.get(modid));
    }

    public static void setStaticServerInstance(MinecraftServer server) {
        SERVER = server;
    }

    public static MinecraftServer getStaticServerInstance() {
        if(SERVER == null) {
            throw new RuntimeException("Server not initialized yet! Call IMDLib.setStaticServerInstance(server) or use IMDLib.entityHandler(modid)");
        }
        return SERVER;
    }

    public static EntityRegistrarHandler entityHandler(String modid) {
        setRegistry(modid);
        return new EntityRegistrarHandler(modid);
    }

}
