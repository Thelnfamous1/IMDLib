package dev.itsmeow.imdlib.entity.interfaces;

import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IVariantTypes<T extends Mob> extends IContainerEntity<T> {

    /* Default Methods */

    default void registerTypeKey() {
        this.getImplementation().getEntityData().define(getContainer().getVariantDataKey(), getContainer().getVariants().get(0).getName());
    }

    default String getVariantString() {
        return this.getImplementation().getEntityData().get(getContainer().getVariantDataKey());
    }

    default IVariantTypes<T> setType(String variantKey) {
        if (!getContainer().getVariantForName(variantKey).isPresent()) {
            variantKey = getRandomType().getName();
        }
        this.getImplementation().getEntityData().set(getContainer().getVariantDataKey(), variantKey);
        return this;
    }

    default IVariantTypes<T> setType(IVariant variant) {
        this.getImplementation().getEntityData().set(getContainer().getVariantDataKey(), variant.getName());
        return this;
    }

    default void writeType(CompoundTag compound) {
        String variantKey = this.getVariantNameOrEmpty();
        if(variantKey.isEmpty()) {
            variantKey = getRandomType().getName();
        }
        compound.putString("VariantId", variantKey);
    }

    default void readType(CompoundTag compound) {
        this.setType(compound.getString("VariantId"));
    }

    default IVariant getOffspringType(IVariantTypes<?> parent1, IVariantTypes<?> parent2) {
        if (parent1 == null || parent2 == null) {
            if (parent1 == null && parent2 != null) {
                return parent2.getVariant().orElseGet(this::getRandomType);
            } else if (parent1 != null) {
                return parent1.getVariant().orElseGet(this::getRandomType);
            } else {
                return this.getRandomType();
            }
        }
        return this.getImplementation().getRandom().nextBoolean() ? parent1.getVariant().orElseGet(() -> parent2.getVariant().orElseGet(this::getRandomType)) : parent2.getVariant().orElseGet(() -> parent1.getVariant().orElseGet(this::getRandomType));
    }

    default IVariant getRandomType() {
        return getContainer().getVariants().get(this.getImplementation().getRandom().nextInt(getContainer().getVariantMax()));
    }

    @Nullable
    default SpawnGroupData initData(LevelAccessor world, MobSpawnType reason, SpawnGroupData livingdata) {
        return dataFromVariant(this.getRandomType(), livingdata);
    }

    @Nullable
    default SpawnGroupData initAgeableData(LevelAccessor world, MobSpawnType reason, SpawnGroupData livingdata) {
        return ageableDataFromVariant(this.getRandomType(), livingdata);
    }

    /**
     * Uses optional to ensure null-safety
     */
    default Optional<IVariant> getVariant() {
        return this.getContainer().getVariantForName(this.getVariantString());
    }

    @Nullable
    default ResourceLocation getVariantTextureOrNull() {
        return getVariant().map(v -> v.getTexture(this.getImplementation())).orElse(null);
    }

    default String getVariantNameOrEmpty() {
        Optional<IVariant> variant = getVariant();
        return variant.isPresent() ? variant.get().getName() : "";
    }

    default SpawnGroupData ageableDataFromVariant(IVariant variant, SpawnGroupData livingdata) {
        if (livingdata instanceof AgeableTypeData) {
            variant = ((AgeableTypeData) livingdata).typeData;
        } else if (livingdata instanceof AgeableMob.AgeableMobGroupData) {
            livingdata = new AgeableTypeData((AgeableMob.AgeableMobGroupData) livingdata, variant);
        } else {
            livingdata = new AgeableTypeData(variant);
        }
        this.setType(variant);
        return livingdata;
    }

    default SpawnGroupData dataFromVariant(IVariant variant, SpawnGroupData livingdata) {
        if (livingdata instanceof TypeData) {
            variant = ((TypeData) livingdata).typeData;
        } else {
            livingdata = new TypeData(variant);
        }
        this.setType(variant);
        return livingdata;
    }

    class TypeData implements SpawnGroupData {
        public IVariant typeData;

        public TypeData(IVariant type) {
            this.typeData = type;
        }
    }

    class AgeableTypeData extends AgeableMob.AgeableMobGroupData {
        public IVariant typeData;
        private int indexInGroup = 0;
        private boolean canBabySpawn = true;
        private float babySpawnProbability = 0.05F;

        public AgeableTypeData(IVariant type) {
            super(false);
            this.typeData = type;
        }

        public AgeableTypeData(AgeableMob.AgeableMobGroupData data, IVariant type) {
            super(false);
            this.typeData = type;
            this.indexInGroup = data.getGroupSize();
            this.canBabySpawn = data.isShouldSpawnBaby();
            this.babySpawnProbability = data.getBabySpawnChance();
        }

        @Override
        public int getGroupSize() {
            return this.indexInGroup;
        }

        @Override
        public boolean isShouldSpawnBaby() {
            return this.canBabySpawn;
        }

        @Override
        public float getBabySpawnChance() {
            return this.babySpawnProbability;
        }


    }
}