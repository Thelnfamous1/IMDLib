package dev.itsmeow.imdlib.entity.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import dev.itsmeow.imdlib.entity.util.builder.AbstractEntityBuilder;
import dev.itsmeow.imdlib.entity.util.builder.EntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;

public class EntityTypeContainer<T extends MobEntity> {

    public EntityType<T> entityType;
    public ModSpawnEggItem egg;

    public final Class<T> entityClass;
    public final String entityName;
    public final EntityClassification spawnType;
    public final Function<World, T> factory;
    public final boolean hasEgg;
    public final int eggColorSolid;
    public final int eggColorSpot;
    public int spawnWeight;
    public int spawnMinGroup;
    public int spawnMaxGroup;
    public boolean biomeVariants = true;
    public boolean doSpawning = true;
    public final float width;
    public final float height;
    public boolean despawn;
    public final EntitySpawnPlacementRegistry.PlacementType placementType;
    public final Heightmap.Type heightMapType;
    public final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
    protected boolean placementRegistered = false;

    protected Biome[] spawnBiomes;

    protected EntityConfiguration config;

    protected final CustomConfigurationHolder customConfig;
    protected final CustomConfigurationHolder customClientConfig;

    protected Supplier<Set<Biome>> defaultBiomeSupplier;

    protected EntityVariantList variantList;
    protected int variantMax;
    protected DataParameter<String> variantDataKey;

    protected final String modid;

    protected EntityTypeContainer(IEntityTypeDefinition<T> def) {
        this.modid = def.getModId();
        this.entityClass = def.getEntityClass();
        this.factory = def.getEntityFactory();
        this.entityName = def.getEntityName();
        this.hasEgg = def.hasEgg();
        this.eggColorSolid = def.getEggSolidColor();
        this.eggColorSpot = def.getEggSpotColor();
        this.spawnWeight = def.getSpawnWeight();
        this.spawnMinGroup = def.getSpawnMinGroup();
        this.spawnMaxGroup = def.getSpawnMaxGroup();
        this.spawnType = def.getSpawnClassification();
        this.width = def.getWidth();
        this.height = def.getHeight();
        this.despawn = def.despawns();
        this.customConfig = def.getCustomConfig();
        this.customClientConfig = def.getCustomClientConfig();
        this.defaultBiomeSupplier = def.getSpawnBiomes();
        this.placementType = def.getPlacementType();
        this.heightMapType = def.getPlacementHeightMapType();
        this.placementPredicate = def.getPlacementPredicate();
        this.variantMax = def.getVariantAmount();
        if(this.hasVariants()) {
            variantList = new EntityVariantList(variantMax);
            variantList.add(def.getVariants());
        }
    }

    public static class Builder<T extends MobEntity> extends AbstractEntityBuilder<T, EntityTypeContainer<T>, Builder<T>> {

        private Builder(Class<T> EntityClass, Function<World, T> func, String entityNameIn, String modid) {
            super(EntityClass, func, entityNameIn, modid);
        }

        public EntityTypeContainer<T> rawBuild() {
            return new EntityTypeContainer<T>(new EntityTypeDefinition<T>(this));
        }

        @Override
        public Builder<T> getImplementation() {
            return this;
        }

        public static <T extends MobEntity> Builder<T> create(Class<T> EntityClass, Function<World, T> func, String entityNameIn, String modid) {
            return new Builder<T>(EntityClass, func, entityNameIn, modid);
        }

    }

    public void initConfiguration(ForgeConfigSpec.Builder builder) {
        this.config = new EntityConfiguration(builder);
    }

    public EntityConfiguration getConfiguration() {
        return this.config;
    }

    public void setBiomes(Biome... biomes) {
        this.spawnBiomes = biomes;
    }

    public Biome[] getBiomes() {
        return spawnBiomes;
    }

    public class EntityConfiguration {
        public ForgeConfigSpec.BooleanValue doSpawning;
        public ForgeConfigSpec.BooleanValue biomeVariants;
        public ForgeConfigSpec.IntValue spawnMinGroup;
        public ForgeConfigSpec.IntValue spawnMaxGroup;
        public ForgeConfigSpec.IntValue spawnWeight;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> biomesList;
        public List<String> biomeStrings;
        public ForgeConfigSpec.BooleanValue doDespawn;

        protected EntityConfiguration(ForgeConfigSpec.Builder builder) {
            EntityTypeContainer<T> container = EntityTypeContainer.this;
            builder.push(container.entityName);
            this.biomeStrings = Arrays.asList(container.getBiomeIDs());
            this.loadSpawning(builder);
            this.loadSpawnValues(builder, container);
            doDespawn = builder.comment("True if this entity can despawn freely when no players are nearby.").worldRestart().define("doDespawn", container.despawn);
            if(ISelectiveVariantTypes.class.isAssignableFrom(EntityTypeContainer.this.entityClass)) {
                biomeVariants = builder.comment("Setting to true enables biome based variant spawning. This will make this entity choose variants based on the biome they spawn in. (No longer affects spawn eggs as it did in the past, only natural spawns)").worldRestart().define("biomeBasedVariants", container.biomeVariants);
            }
            EntityTypeContainer.this.customConfigurationInit(builder);
            builder.pop();
        }

        public void loadSpawning(ForgeConfigSpec.Builder builder) {
            doSpawning = builder.comment("Disables natural spawning").worldRestart().define("doSpawning", true);

        }

        public void loadSpawnValues(ForgeConfigSpec.Builder builder, EntityTypeContainer<T> container) {
            spawnWeight = builder.comment("The spawn chance compared to other entities (typically between 6-20)").worldRestart().defineInRange("weight", container.spawnWeight, 1, 9999);
            spawnMinGroup = builder.comment("Must be greater than 0").worldRestart().defineInRange("minGroup", container.spawnMinGroup, 1, 9999);
            spawnMaxGroup = builder.comment("Must be greater or equal to min value!").worldRestart().defineInRange("maxGroup", container.spawnMaxGroup, 1, 9999);
            biomesList = builder.comment("Enter biome Resource Locations. Supports modded biomes.").worldRestart().defineList("spawnBiomes", biomeStrings, (Predicate<Object>) input -> input instanceof String);
        }
    }

    public String[] getBiomeIDs() {
        try {
            spawnBiomes = defaultBiomeSupplier.get().toArray(new Biome[0]);
        } catch(NullPointerException e) {
            spawnBiomes = new Biome[0];
        }
        String[] biomeStrings = new String[spawnBiomes.length];
        for(int i = 0; i < spawnBiomes.length; i++) {
            biomeStrings[i] = spawnBiomes[i].getRegistryName().toString();
        }
        return biomeStrings;
    }

    public void configurationLoad() {
        EntityTypeContainer<T>.EntityConfiguration section = this.getConfiguration();
        spawnMaxGroup = section.spawnMaxGroup.get();
        spawnMinGroup = section.spawnMinGroup.get();
        spawnWeight = section.spawnWeight.get();
        doSpawning = section.doSpawning.get();
        despawn = section.doDespawn.get();
        if(section.biomeVariants != null) {
            this.biomeVariants = section.biomeVariants.get();
        }
        if(this.customConfig != null) {
            this.customConfig.customConfigurationLoad();
        }
    }

    public void clientConfigurationLoad() {
        if(this.customClientConfig != null) {
            this.customClientConfig.customConfigurationLoad();
        }
    }

    public void customConfigurationInit(ForgeConfigSpec.Builder builder) {
        if(this.customConfig != null) {
            this.customConfig.customConfigurationInit(builder);
        }
    }

    public void clientCustomConfigurationInit(ForgeConfigSpec.Builder builder) {
        if(this.customClientConfig != null) {
            builder.push(this.entityName);
            this.customClientConfig.customConfigurationInit(builder);
            builder.pop();
        }
    }

    public static interface CustomConfigurationHolder {
        void customConfigurationInit(ForgeConfigSpec.Builder builder);

        void customConfigurationLoad();
    }

    public static <T extends MobEntity> boolean waterSpawn(EntityType<T> type, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
        return pos.getY() > 45 && pos.getY() < (world.getSeaLevel() - 1) && world.getBlockState(pos).getBlock() == Blocks.WATER;
    }

    public void registerPlacement() {
        if(this.placementType != null && this.placementPredicate != null && this.heightMapType != null) {
            if(!placementRegistered) {
                EntitySpawnPlacementRegistry.<T>register(this.entityType, placementType, heightMapType, placementPredicate);
                placementRegistered = true;
            }
        }
    }

    public String getModId() {
        return modid;
    }

    public boolean hasVariants() {
        return this.variantMax > 0;
    }

    public int getVariantMax() {
        return this.variantMax;
    }

    @Nullable
    @CheckForNull
    public IVariant getVariantForName(String name) {
        return this.variantList.getVariantForName(name);
    }

    @Deprecated
    public IVariant getVariantForIndex(int index) {
        return this.variantList.getVariantForIndex(index);
    }

    public ImmutableList<IVariant> getVariants() {
        return this.variantList.getVariantList();
    }

    @Deprecated
    public int getVariantIndex(IVariant variant) {
        return this.variantList.getVariantIndex(variant);
    }

    public DataParameter<String> getVariantDataKey() {
        if(this.variantDataKey == null) {
            this.variantDataKey = EntityDataManager.<String>createKey(this.entityClass, DataSerializers.STRING);
        }
        return this.variantDataKey;
    }
}
