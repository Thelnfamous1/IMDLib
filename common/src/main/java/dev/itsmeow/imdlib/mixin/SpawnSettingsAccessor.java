package dev.itsmeow.imdlib.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(MobSpawnSettings.class)
public interface SpawnSettingsAccessor {

    @Accessor("spawners")
    Map<MobCategory, List<MobSpawnSettings.SpawnerData>> getSpawners();

    @Mutable
    @Accessor("spawners")
    void setSpawners(Map<MobCategory, List<MobSpawnSettings.SpawnerData>> spawners);

    @Accessor("mobSpawnCosts")
    Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getMobSpawnCosts();

    @Mutable
    @Accessor("mobSpawnCosts")
    void setMobSpawnCosts(Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> costs);
}
