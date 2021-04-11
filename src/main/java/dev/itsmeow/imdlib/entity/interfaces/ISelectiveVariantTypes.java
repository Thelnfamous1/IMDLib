package dev.itsmeow.imdlib.entity.interfaces;

import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Set;

public interface ISelectiveVariantTypes<T extends MobEntity> extends IVariantTypes<T> {

    @Nullable
    @Override
    default ILivingEntityData initData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        return useSelectiveTypes(reason) ? dataFromVariant(getRandomVariantForBiome(world, reason), livingdata) : IVariantTypes.super.initData(world, reason, livingdata);
    }

    @Nullable
    @Override
    default ILivingEntityData initAgeableData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        return useSelectiveTypes(reason) ? ageableDataFromVariant(getRandomVariantForBiome(world, reason), livingdata) : IVariantTypes.super.initAgeableData(world, reason, livingdata);
    }

    String[] getTypesFor(Biome biome, Set<BiomeDictionary.Type> types, SpawnReason reason);

    default boolean useSelectiveTypes() {
        return this.getContainer().getConfiguration().biomeVariants.get();
    }

    default boolean useSelectiveTypes(SpawnReason reason) {
        return this.useSelectiveTypes() && (reason == SpawnReason.CHUNK_GENERATION || reason == SpawnReason.NATURAL);
    }

    @Nullable
    @CheckForNull
    default IVariant getRandomVariantForBiome(IWorld world, SpawnReason reason) {
        Biome biome = world.getBiome(this.getImplementation().getPosition());
        String[] validTypes = this.getTypesFor(biome, BiomeDictionary.getTypes(biome), reason);
        String varStr = validTypes[this.getImplementation().getRNG().nextInt(validTypes.length)];
        IVariant variant = this.getContainer().getVariantForName(varStr);
        if(variant == null || !varStr.equals(variant.getName())) {
            throw new RuntimeException("Received invalid variant \"" + varStr  + "\" from selective type on entity " + this.getContainer().getEntityName());
        }
        return variant;
    }
}