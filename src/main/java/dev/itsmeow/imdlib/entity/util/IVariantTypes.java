package dev.itsmeow.imdlib.entity.util;

import javax.annotation.Nullable;

import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;

public interface IVariantTypes<T extends MobEntity> extends IContainerEntity<T> {

    /* Default Methods */

    default void registerTypeKey() {
        this.getImplementation().getDataManager().register(getContainer().getVariantDataKey(), "");
    }

    default String getVariantString() {
        return this.getImplementation().getDataManager().get(getContainer().getVariantDataKey());
    }

    default IVariantTypes<T> setType(String variantKey) {
        this.getImplementation().getDataManager().set(getContainer().getVariantDataKey(), this.getContainer().getVariant(variantKey).getName());
        return this;
    }

    default IVariantTypes<T> setType(IVariant variant) {
        this.getImplementation().getDataManager().set(getContainer().getVariantDataKey(), variant.getName());
        return this;
    }

    default void writeType(CompoundNBT compound) {
        compound.putString("VariantId", this.getVariant().getName());
    }

    default void readType(CompoundNBT compound) {
        this.setType(compound.getString("VariantId"));
    }

    default IVariant getOffspringType(IVariantTypes<?> parent1, IVariantTypes<?> parent2) {
        return this.getImplementation().getRNG().nextBoolean() ? parent1.getVariant() : parent2.getVariant();
    }

    default IVariant getRandomType() {
        return getContainer().getVariant(this.getImplementation().getRNG().nextInt(getContainer().getVariantMax()));
    }

    public static class TypeData implements ILivingEntityData {
        public String typeData;

        public TypeData(String type) {
            this.typeData = type;
        }
    }

    @Nullable
    default ILivingEntityData initData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        String variant = this.getRandomType().getName();
        if(livingdata instanceof TypeData) {
            variant = ((TypeData) livingdata).typeData;
        } else {
            livingdata = new TypeData(variant);
        }
        this.setType(variant);
        return livingdata;
    }

    default IVariant getVariant() {
        return this.getContainer().getVariant(this.getVariantString());
    }

    default ResourceLocation getVariantTexture() {
        return getVariant().getTexture();
    }

    default String getVariantName() {
        return getVariant().getName();
    }

}