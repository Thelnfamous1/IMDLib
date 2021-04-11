package dev.itsmeow.imdlib.entity.interfaces;

import java.util.Optional;

import javax.annotation.Nullable;

import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.entity.AgeableEntity.AgeableData;
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
            } else if(parent1 != null) {
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

    class TypeData implements ILivingEntityData {
        public IVariant typeData;

        public TypeData(IVariant type) {
            this.typeData = type;
        }
    }

    class AgeableTypeData extends AgeableData {
        public IVariant typeData;
        private int indexInGroup = 0;
        private boolean canBabySpawn = true;
        private float babySpawnProbability = 0.05F;

        public AgeableTypeData(IVariant type) {
            super(false);
            this.typeData = type;
        }

        public AgeableTypeData(AgeableData data, IVariant type) {
            super(false);
            this.typeData = type;
            this.indexInGroup = data.getIndexInGroup();
            this.canBabySpawn = data.canBabySpawn();
            this.babySpawnProbability = data.getBabySpawnProbability();
        }

        @Override
        public int getIndexInGroup() {
            return this.indexInGroup;
        }

        @Override
        public void incrementIndexInGroup() {
            ++this.indexInGroup;
        }

        @Override
        public boolean canBabySpawn() {
            return this.canBabySpawn;
        }

        @Override
        public float getBabySpawnProbability() {
            return this.babySpawnProbability;
        }

    }

    @Nullable
    default ILivingEntityData initData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        return dataFromVariant(this.getRandomType(), livingdata);
    }

    @Nullable
    default ILivingEntityData initAgeableData(IWorld world, SpawnReason reason, ILivingEntityData livingdata) {
        return ageableDataFromVariant(this.getRandomType(), livingdata);
    }

    /**
     * Uses optional to ensure null-safety
     */
    default Optional<IVariant> getVariant() {
        return Optional.ofNullable(this.getContainer().getVariantForName(this.getVariantString()));
    }

    @Nullable
    default ResourceLocation getVariantTextureOrNull() {
        return getVariant().map(v -> v.getTexture(this.getImplementation())).orElse(null);
    }

    default String getVariantNameOrEmpty() {
        Optional<IVariant> variant = getVariant();
        return variant.isPresent() ? variant.get().getName() : "";
    }

    default ILivingEntityData ageableDataFromVariant(IVariant variant, ILivingEntityData livingdata) {
        if(livingdata instanceof AgeableTypeData) {
            variant = ((AgeableTypeData) livingdata).typeData;
        } else if(livingdata instanceof AgeableData) {
            livingdata = new AgeableTypeData((AgeableData) livingdata, variant);
        } else {
            livingdata = new AgeableTypeData(variant);
        }
        this.setType(variant);
        return livingdata;
    }

    default ILivingEntityData dataFromVariant(IVariant variant, ILivingEntityData livingdata) {
        if(livingdata instanceof TypeData) {
            variant = ((TypeData) livingdata).typeData;
        } else {
            livingdata = new TypeData(variant);
        }
        this.setType(variant);
        return livingdata;
    }
}