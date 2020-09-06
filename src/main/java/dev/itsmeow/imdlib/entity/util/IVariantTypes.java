package dev.itsmeow.imdlib.entity.util;

import java.util.Optional;

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
        if(getContainer().getVariantForName(variantKey) == null) {
            variantKey = getRandomType().getName();
        }
        this.getImplementation().getDataManager().set(getContainer().getVariantDataKey(), variantKey);
        return this;
    }

    default IVariantTypes<T> setType(IVariant variant) {
        this.getImplementation().getDataManager().set(getContainer().getVariantDataKey(), variant.getName());
        return this;
    }

    default void writeType(CompoundNBT compound) {
        compound.putString("VariantId", this.getVariantNameOrEmpty());
    }

    default void readType(CompoundNBT compound) {
        this.setType(compound.getString("VariantId"));
    }

    default IVariant getOffspringType(IVariantTypes<?> parent1, IVariantTypes<?> parent2) {
        if(parent1 == null || parent2 == null) {
            if(parent1 == null && parent2 != null) {
                return parent2.getVariant().orElseGet(this::getRandomType);
            } else if(parent2 == null && parent1 != null) {
                return parent1.getVariant().orElseGet(this::getRandomType);
            } else {
                return this.getRandomType();
            }
        }
        return this.getImplementation().getRNG().nextBoolean() ? parent1.getVariant().orElseGet(() -> parent2.getVariant().orElseGet(this::getRandomType)) : parent2.getVariant().orElseGet(() -> parent1.getVariant().orElseGet(this::getRandomType));
    }

    default IVariant getRandomType() {
        return getContainer().getVariants().get(this.getImplementation().getRNG().nextInt(getContainer().getVariantMax()));
    }

    public static class TypeData implements ILivingEntityData {
        public IVariant typeData;

        public TypeData(IVariant type) {
            this.typeData = type;
        }
    }

    @Nullable
    default ILivingEntityData initData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        IVariant variant = this.getRandomType();
        if(livingdata instanceof TypeData) {
            variant = ((TypeData) livingdata).typeData;
        } else {
            livingdata = new TypeData(variant);
        }
        this.setType(variant);
        return livingdata;
    }

    /**
     * Uses optional to ensure null-safety
     */
    default Optional<IVariant> getVariant() {
        return Optional.ofNullable(this.getContainer().getVariantForName(this.getVariantString()));
    }

    @Nullable
    default ResourceLocation getVariantTextureOrNull() {
        Optional<IVariant> variant = getVariant();
        return variant.isPresent() ? variant.get().getTexture(this.getImplementation()) : null;
    }

    default String getVariantNameOrEmpty() {
        Optional<IVariant> variant = getVariant();
        return variant.isPresent() ? variant.get().getName() : "";
    }

}