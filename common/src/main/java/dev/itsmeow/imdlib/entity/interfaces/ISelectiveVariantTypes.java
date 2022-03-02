package dev.itsmeow.imdlib.entity.interfaces;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface ISelectiveVariantTypes<T extends Mob> extends IVariantTypes<T> {

    @Nullable
    @Override
    default SpawnGroupData initData(LevelAccessor world, MobSpawnType reason, SpawnGroupData livingdata) {
        return useSelectiveTypes(reason) ? dataFromVariant(getRandomVariantForBiome(world, reason), livingdata) : IVariantTypes.super.initData(world, reason, livingdata);
    }

    @Nullable
    @Override
    default SpawnGroupData initAgeableData(LevelAccessor world, MobSpawnType reason, SpawnGroupData livingdata) {
        return useSelectiveTypes(reason) ? ageableDataFromVariant(getRandomVariantForBiome(world, reason), livingdata) : IVariantTypes.super.initAgeableData(world, reason, livingdata);
    }

    String[] getTypesFor(ResourceKey<Biome> biomeKey, Biome biome, Set<BiomeTypes.Type> types, MobSpawnType reason);

    default boolean useSelectiveTypes() {
        return this.getContainer().getConfiguration().biomeVariants.get();
    }

    default boolean useSelectiveTypes(MobSpawnType reason) {
        return this.useSelectiveTypes() && (reason == MobSpawnType.CHUNK_GENERATION || reason == MobSpawnType.NATURAL);
    }

    @Nullable
    default IVariant getRandomVariantForBiome(LevelAccessor world, MobSpawnType reason) {
        Holder<Biome> biome = world.getBiome(this.getImplementation().blockPosition());
        Optional<ResourceKey<Biome>> biomeKey = biome.unwrapKey();
        biomeKey.orElseThrow(() -> new RuntimeException("Biome provided to selective type generation has no ID found."));
        String[] validTypes = this.getTypesFor(biomeKey.get(), biome.value(), BiomeTypes.getTypes(biomeKey.get()), reason);
        String varStr = validTypes[this.getImplementation().getRandom().nextInt(validTypes.length)];
        Optional<IVariant> variant = this.getContainer().getVariantForName(varStr);
        if (!variant.isPresent() || !varStr.equals(variant.get().getName())) {
            throw new RuntimeException("Received invalid variant \"" + varStr + "\" from selective type on entity " + this.getContainer().getEntityName());
        }
        return variant.get();
    }
}