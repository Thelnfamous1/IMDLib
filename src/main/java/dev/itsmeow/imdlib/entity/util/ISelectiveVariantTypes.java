package dev.itsmeow.imdlib.entity.util;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public interface ISelectiveVariantTypes<T extends MobEntity> extends IVariantTypes<T> {

    @Nullable
    @Override
    default ILivingEntityData initData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        if(this.getContainer().biomeVariants && (reason == SpawnReason.CHUNK_GENERATION || reason == SpawnReason.NATURAL)) {
            if(!this.getImplementation().isChild()) {
                Biome biome = world.getBiome(this.getImplementation().getPosition());
                String[] validTypes = this.getTypesFor(biome, BiomeDictionary.getTypes(biome));
                String varStr = validTypes[this.getImplementation().getRNG().nextInt(validTypes.length)];
                IVariant variant = this.getContainer().getVariantForName(varStr);
                if(variant == null || variant == EntityVariantList.EMPTY_VARIANT) {
                    throw new RuntimeException("Received invalid variant string from selective type: " + varStr + " on entity " + this.getContainer().entityName);
                }
                if(livingdata instanceof TypeData) {
                    variant = ((TypeData) livingdata).typeData;
                } else {
                    livingdata = new TypeData(variant);
                }
                this.setType(variant);
            }
        } else {
            if(!this.getImplementation().isChild()) {
                IVariant variant = this.getRandomType();
                if(livingdata instanceof TypeData) {
                    variant = ((TypeData) livingdata).typeData;
                } else {
                    livingdata = new TypeData(variant);
                }
                this.setType(variant);
            }
        }
        return livingdata;
    }

    String[] getTypesFor(Biome biome, Set<BiomeDictionary.Type> types);

}