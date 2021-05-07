package dev.itsmeow.imdlib.config;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.mixin.SpawnSettingsAccessor;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForgeConfig implements Config {

    public ServerEntityConfiguration serverConfig(ForgeConfigSpec.Builder builder) {
        return new ServerEntityConfiguration(builder);
    }

    public ClientEntityConfiguration clientConfig(ForgeConfigSpec.Builder builder) {
        return new ClientEntityConfiguration(builder);
    }

    public static class ServerEntityConfiguration {
        public EntityRegistrarHandler handler;

        public ServerEntityConfiguration(ForgeConfigSpec.Builder builder, EntityRegistrarHandler handler) {
            builder.push("entities");
            this.handler = handler;
            {
                handler.ENTITIES.values().forEach(c -> c.createConfiguration(builder));
            }
            builder.pop();
        }

        public void onLoad() {
            // Update entity data
            ENTITIES.values().forEach(e -> e.getConfiguration().load());

            if (Platform.getEnv() == Dist.DEDICATED_SERVER) {
                me.shedaniel.architectury.registry.@NotNull Registry<Biome> biomeRegistry = IMDLib.REGISTRIES.get().get(Registry.BIOME_REGISTRY);
                for (Biome biome : biomeRegistry) {
                    MobSpawnSettings spawnInfo = biome.getMobSettings();
                    // make spawns mutable
                    ((SpawnSettingsAccessor) spawnInfo).setSpawners(new HashMap<MobCategory, List<MobSpawnSettings.SpawnerData>>((SpawnSettingsAccessor) spawnInfo));
                    // make spawner lists mutable
                    for (MobCategory classification : MobCategory.values()) {
                        ArrayList<MobSpawnSettings.SpawnerData> newList = new ArrayList<>();
                        List<MobSpawnSettings.SpawnerData> oldList = spawnInfo.spawners.get(classification);
                        if (oldList != null) {
                            newList.addAll(oldList);
                        }
                        ((SpawnSettingsAccessor) spawnInfo).getSpawners().put(classification, newList);
                    }
                    // make costs mutable
                    spawnInfo.spawnCosts = new HashMap<>(spawnInfo.spawnCosts);
                    for (EntityTypeContainer<?> entry : ENTITIES.values()) {
                        EntityTypeContainer<?>.EntityConfiguration config = entry.getConfiguration();
                        if (config.doSpawning.get() && config.spawnWeight.get() > 0 && entry.getBiomeIDs().contains(key.toString())) {
                            entry.registerPlacement();
                            List<MobSpawnSettings.SpawnerData> list = spawnInfo.spawners.get(entry.getDefinition().getSpawnClassification());
                            if (list != null) {
                                list.add(entry.getSpawnEntry());
                            }
                            if (config.spawnCostPer.get() != 0 && config.spawnMaxCost.get() != 0 && entry.getSpawnCostBiomeIDs().contains(key.toString())) {
                                // stupid private constructors
                                MobSpawnSettings.MobSpawnCost costs = new MobSpawnSettings.Builder().withSpawnCost(entry.getEntityType(), config.spawnCostPer.get(), config.spawnMaxCost.get()).build().spawnCosts.get(entry.getEntityType());
                                spawnInfo.spawnCosts.put(entry.getEntityType(), costs);
                            }
                        }
                    }
                }
            }
        }

    }

    public static class ClientEntityConfiguration {

        public ClientEntityConfiguration(ForgeConfigSpec.Builder builder) {
            builder.comment("This is the CLIENT SIDE configuration for " + modid + ".",
                    "To configure SERVER values (spawning, behavior, etc), go to:",
                    "saves/(world)/serverconfig/" + modid + "-server.toml",
                    "or, on a dedicated server:",
                    "(world)/serverconfig/" + modid + "-server.toml");
            builder.push("entities");
            {
                ENTITIES.values().forEach(c -> c.clientCustomConfigurationInit(builder));
            }
            builder.pop();
        }

        public void onLoad() {
            ENTITIES.values().forEach(EntityTypeContainer::clientCustomConfigurationLoad);
        }

    }
}
