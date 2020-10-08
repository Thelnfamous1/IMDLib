package dev.itsmeow.imdlib.entity.util.builder;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainer.CustomConfigurationHolder;
import dev.itsmeow.imdlib.entity.util.EntityVariant;
import dev.itsmeow.imdlib.entity.util.IVariant;
import dev.itsmeow.imdlib.util.BiomeListBuilder;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;

public abstract class AbstractEntityBuilder<T extends MobEntity, C extends EntityTypeContainer<T>, B extends AbstractEntityBuilder<T, C, B>> implements IEntityBuilder<T, C, B> {
    protected final Class<T> entityClass;
    protected final String entityName;
    protected final Function<World, T> factory;
    protected EntityClassification spawnType;
    protected int eggColorSolid;
    protected int eggColorSpot;
    protected int spawnWeight;
    protected int spawnMinGroup;
    protected int spawnMaxGroup;
    protected float width;
    protected float height;
    protected boolean despawn;
    protected CustomConfigurationHolder customConfig;
    protected CustomConfigurationHolder customClientConfig;
    protected Supplier<Set<Biome>> defaultBiomeSupplier;
    protected EntitySpawnPlacementRegistry.PlacementType placementType;
    protected Heightmap.Type heightMapType;
    protected EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
    protected int variantCount = 0;
    protected IVariant[] variants;
    protected final String modid;
    protected boolean hasEgg;
    protected Function<C, HeadType> headTypeBuilder;

    protected AbstractEntityBuilder(Class<T> EntityClass, Function<World, T> func, String entityNameIn, String modid) {
        this.entityClass = EntityClass;
        this.factory = func;
        this.entityName = entityNameIn;
        this.modid = modid;
        this.eggColorSolid = 0x000000;
        this.eggColorSpot = 0xffffff;
        this.spawnWeight = 1;
        this.spawnMinGroup = 1;
        this.spawnMaxGroup = 1;
        this.spawnType = EntityClassification.CREATURE;
        this.width = 1;
        this.height = 1;
        this.despawn = false;
        this.hasEgg = false;
        this.customConfig = null;
        this.defaultBiomeSupplier = () -> new HashSet<Biome>();
        this.placementType = null;
        this.heightMapType = null;
        this.placementPredicate = null;
    }

    public abstract B getImplementation();

    @Override
    public B spawn(EntityClassification type, int weight, int min, int max) {
        this.spawnType = type;
        this.spawnWeight = weight;
        this.spawnMinGroup = min;
        this.spawnMaxGroup = max;
        return getImplementation();
    }

    @Override
    public B egg(int solid, int spot) {
        this.hasEgg = true;
        this.eggColorSolid = solid;
        this.eggColorSpot = spot;
        return getImplementation();
    }

    @Override
    public B size(float width, float height) {
        this.width = width;
        this.height = height;
        return getImplementation();
    }

    @Override
    public B despawn() {
        this.despawn = true;
        return getImplementation();
    }

    @Override
    public B config(CustomConfigurationHolder config) {
        this.customConfig = config;
        return getImplementation();
    }

    @Override
    public B clientConfig(CustomConfigurationHolder config) {
        this.customClientConfig = config;
        return getImplementation();
    }

    @Override
    public B placement(EntitySpawnPlacementRegistry.PlacementType type, Heightmap.Type heightMap, EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate) {
        this.placementType = type;
        this.heightMapType = heightMap;
        this.placementPredicate = predicate;
        return getImplementation();
    }
    @Override
    public B defaultPlacement(EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate) {
        return placement(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, predicate);
    }

    @Override
    public B waterPlacement() {
        return placement(EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityTypeContainer::waterSpawn);
    }

    @Override
    public B waterPlacement(EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate) {
        return placement(EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, predicate);
    }

    @Override
    public B biomes(BiomeDictionary.Type... biomeTypes) {
        this.defaultBiomeSupplier = toBiomes(biomeTypes);
        return getImplementation();
    }

    @Override
    public B biomes(Supplier<Biome[]> biomes) {
        this.defaultBiomeSupplier = toBiomes(biomes);
        return getImplementation();
    }

    @Override
    public B biomes(Function<BiomeListBuilder, BiomeListBuilder> biomes) {
        return biomes(biomes.apply(BiomeListBuilder.create())::collect);
    }

    @Override
    public B variants(IVariant... variants) {
        this.variantCount = variants.length;
        this.variants = variants;
        return getImplementation();
    }

    @Override
    public B variants(String... nameTextures) {
        this.variantCount = nameTextures.length;
        this.variants = new EntityVariant[nameTextures.length];
        for(int i = 0; i < nameTextures.length; i++) {
            String nameTex = nameTextures[i];
            variants[i] = new EntityVariant(modid, nameTex, this.entityName + "_" + nameTex);
        }
        return getImplementation();
    }

    @Override
    public B variants(int max) {
        if(max > 0) {
            this.variantCount = max;
            this.variants = new EntityVariant[max];
            for(int i = 0; i < max; i++) {
                String nameTex = String.valueOf(i + 1);
                variants[i] = new EntityVariant(modid, nameTex, this.entityName + "_" + nameTex);
            }
        } else {
            throw new RuntimeException("what are you doing kid");
        }
        return getImplementation();
    }

    @Override
    public B variants(Function<String, IVariant> constructor, String... variants) {
        this.variantCount = variants.length;
        IVariant[] variantList = new IVariant[variantCount];
        for(int i = 0; i < variantCount; i++) {
            variantList[i] = constructor.apply(variants[i]);
        }
        this.variants = variantList;
        return getImplementation();
    }

    protected static Supplier<Set<Biome>> toBiomes(BiomeDictionary.Type[] biomeTypes) {
        return () -> {
            Set<Biome> biomes = new HashSet<Biome>();
            for(BiomeDictionary.Type type : biomeTypes) {
                biomes.addAll(BiomeDictionary.getBiomes(type));
            }
            return biomes;
        };
    }

    protected static Supplier<Set<Biome>> toBiomes(Supplier<Biome[]> biomes2) {
        return () -> {
            Set<Biome> biomes = new HashSet<Biome>();
            biomes.addAll(Lists.newArrayList(biomes2.get()));
            return biomes;
        };
    }

    @Override
    public HeadType.Builder<T, C, B> head(String headName) {
        return new HeadType.Builder<T, C, B>(getImplementation(), headName);
    }

    @Override
    public HeadType.Builder<T, C, B> head() {
        return head(this.entityName + "head");
    }

    @Override
    public void postBuild(C container) {
        if(this.headTypeBuilder != null) {
            container.headType = headTypeBuilder.apply(container);
        }
    }

    @Override
    public void setHeadBuild(Function<C, HeadType> builder) {
        this.headTypeBuilder = builder;
    }

    @Override
    public String getMod() {
        return modid;
    }
}
