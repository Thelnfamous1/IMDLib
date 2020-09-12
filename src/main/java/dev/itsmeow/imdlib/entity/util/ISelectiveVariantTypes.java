package dev.itsmeow.imdlib.entity.util;

import java.util.Set;

import javax.annotation.Nullable;

import dev.itsmeow.imdlib.util.BiomeDictionary;
import net.minecraft.entity.AgeableEntity.AgeableData;
import net.minecraft.util.RegistryKey;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public interface ISelectiveVariantTypes<T extends MobEntity> extends IVariantTypes<T> {

    @Nullable
    @Override
    default ILivingEntityData initData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        if(this.getContainer().biomeVariants && (reason == SpawnReason.CHUNK_GENERATION || reason == SpawnReason.NATURAL)) {
            Biome biome = world.getBiome(this.getImplementation().getPosition());
            String[] validTypes = this.getTypesFor(biome, BiomeDictionary.getTypes(RegistryKey.getOrCreateKey(Keys.BIOMES, biome.getRegistryName())));
            String varStr = validTypes[this.getImplementation().getRNG().nextInt(validTypes.length)];
            IVariant variant = this.getContainer().getVariantForName(varStr);
            if(variant == null || !varStr.equals(variant.getName())) {
                throw new RuntimeException("Received invalid variant string from selective type: " + varStr + " on entity " + this.getContainer().entityName);
            }
            if(livingdata instanceof TypeData) {
                variant = ((TypeData) livingdata).typeData;
            } else {
                livingdata = new TypeData(variant);
            }
            this.setType(variant);
        } else {
            IVariant variant = this.getRandomType();
            if(livingdata instanceof TypeData) {
                variant = ((TypeData) livingdata).typeData;
            } else {
                livingdata = new TypeData(variant);
            }
            this.setType(variant);
        }
        return livingdata;
    }

    @Nullable
    @Override
    default ILivingEntityData initAgeableData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        if(!this.getImplementation().isChild()) {
            if(this.getContainer().biomeVariants && (reason == SpawnReason.CHUNK_GENERATION || reason == SpawnReason.NATURAL)) {
                Biome biome = world.getBiome(this.getImplementation().getPosition());
                String[] validTypes = this.getTypesFor(biome, BiomeDictionary.getTypes(RegistryKey.getOrCreateKey(Keys.BIOMES, biome.getRegistryName())));
                String varStr = validTypes[this.getImplementation().getRNG().nextInt(validTypes.length)];
                IVariant variant = this.getContainer().getVariantForName(varStr);
                if(livingdata instanceof AgeableTypeData) {
                    variant = ((AgeableTypeData) livingdata).typeData;
                } else if(livingdata instanceof AgeableData) {
                    livingdata = new AgeableTypeData((AgeableData) livingdata, variant);
                } else {
                    livingdata = new AgeableTypeData(variant);
                }
                this.setType(variant);
            } else {
                IVariant variant = this.getRandomType();
                if(livingdata instanceof AgeableTypeData) {
                    variant = ((AgeableTypeData) livingdata).typeData;
                } else if(livingdata instanceof AgeableData) {
                    livingdata = new AgeableTypeData((AgeableData) livingdata, variant);
                } else {
                    livingdata = new AgeableTypeData(variant);
                }
                this.setType(variant);
            }
        }
        return livingdata;
    }

    String[] getTypesFor(Biome biome, Set<BiomeDictionary.Type> types);

}