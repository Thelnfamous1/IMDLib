package dev.itsmeow.imdlib.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.entity.util.variant.EntityVariant;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.BiomeListBuilder;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractEntityBuilder<T extends MobEntity, C extends EntityTypeContainer<T>, B extends AbstractEntityBuilder<T, C, B>> implements IEntityBuilder<T, C, B> {
    protected final Class<T> entityClass;
    protected final String entityName;
    protected final EntityType.IFactory<T> factory;
    public boolean hasSpawns;
    protected EntityClassification spawnType;
    protected int eggColorSolid;
    protected int eggColorSpot;
    protected int spawnWeight;
    protected boolean useSpawnCosts;
    protected int spawnMinGroup;
    protected int spawnMaxGroup;
    protected double spawnCostPer;
    protected double spawnMaxCost;
    protected float width;
    protected float height;
    protected boolean despawn;
    protected CustomConfigurationLoad customConfigLoad;
    protected CustomConfigurationInit customConfigInit;
    protected CustomConfigurationLoad customClientConfigLoad;
    protected CustomConfigurationInit customClientConfigInit;
    protected Supplier<Set<RegistryKey<Biome>>> defaultBiomeSupplier;
    protected EntitySpawnPlacementRegistry.PlacementType placementType;
    protected Heightmap.Type heightMapType;
    protected EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
    protected int variantCount = 0;
    protected IVariant[] variants;
    protected final String modid;
    protected boolean hasEgg;
    protected final Supplier<AttributeModifierMap.MutableAttribute> attributeMap;
    protected Function<C, HeadType> headTypeBuilder;

    protected AbstractEntityBuilder(Class<T> EntityClass, EntityType.IFactory<T> factory, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
        this.entityClass = EntityClass;
        this.factory = factory;
        this.entityName = entityNameIn;
        this.modid = modid;
        this.eggColorSolid = 0x000000;
        this.eggColorSpot = 0xffffff;
        this.spawnWeight = 1;
        this.spawnMinGroup = 1;
        this.spawnMaxGroup = 1;
        this.useSpawnCosts = false;
        this.spawnCostPer = 1;
        this.spawnMaxCost = 10;
        this.spawnType = EntityClassification.CREATURE;
        this.hasSpawns = false;
        this.width = 1;
        this.height = 1;
        this.despawn = false;
        this.hasEgg = false;
        this.defaultBiomeSupplier = HashSet::new;
        this.placementType = null;
        this.heightMapType = null;
        this.placementPredicate = null;
        this.attributeMap = attributeMap;
    }

    public abstract B getImplementation();

    @Override
    public B spawn(EntityClassification type, int weight, int min, int max) {
        this.hasSpawns = true;
        this.spawnType = type;
        this.spawnWeight = weight;
        this.spawnMinGroup = min;
        this.spawnMaxGroup = max;
        return getImplementation();
    }

    @Override
    public B spawnCosts(double cost, double maxCost) {
        if(!hasSpawns) {
            throw new RuntimeException("You must specify spawns before spawn costs");
        }
        this.useSpawnCosts = true;
        this.spawnCostPer = cost;
        this.spawnMaxCost = maxCost;
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
    public B config(CustomConfigurationInit configurationInit) {
        return config(configurationInit, holder -> {});
    }

    @Override
    public B clientConfig(CustomConfigurationInit configurationInit) {
        return clientConfig(configurationInit, holder -> {});
    }

    @Override
    public B config(CustomConfigurationInit configurationInit, CustomConfigurationLoad configurationLoad) {
        this.customConfigLoad = configurationLoad;
        this.customConfigInit = configurationInit;
        return getImplementation();
    }

    @Override
    public B clientConfig(CustomConfigurationInit configurationInit, CustomConfigurationLoad configurationLoad) {
        this.customClientConfigLoad = configurationLoad;
        this.customClientConfigInit = configurationInit;
        return getImplementation();
    }

    @Override
    public B placement(EntitySpawnPlacementRegistry.PlacementType type, Heightmap.Type heightMap, EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate) {
        if(!hasSpawns) {
            throw new RuntimeException("You must specify spawns before placement");
        }
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
        if(!hasSpawns) {
            throw new RuntimeException("You must specify spawns before biomes");
        }
        this.defaultBiomeSupplier = toBiomes(biomeTypes, false);
        return getImplementation();
    }

    @Override
    public B biomesOverworld(BiomeDictionary.Type... biomeTypes) {
        if(!hasSpawns) {
            throw new RuntimeException("You must specify spawns before biomes");
        }
        this.defaultBiomeSupplier = toBiomes(biomeTypes, true);
        return getImplementation();
    }

    @Override
    public B biomes(Supplier<RegistryKey<Biome>[]> biomes) {
        if(!hasSpawns) {
            throw new RuntimeException("You must specify spawns before biomes");
        }
        this.defaultBiomeSupplier = () -> Sets.newHashSet(biomes.get());
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

    protected static Supplier<Set<RegistryKey<Biome>>> toBiomes(BiomeDictionary.Type[] biomeTypes, boolean overworldOnly) {
        return () -> Lists.newArrayList(biomeTypes).stream().flatMap(type -> BiomeDictionary.getBiomes(type).stream()).filter(b -> !overworldOnly || BiomeDictionary.hasType(b, BiomeDictionary.Type.OVERWORLD)).collect(Collectors.toSet());
    }

    @Override
    public HeadType.Builder<T, C, B> head(String headName) {
        return new HeadType.Builder<>(getImplementation(), headName);
    }

    @Override
    public HeadType.Builder<T, C, B> head() {
        return head(this.entityName + "head");
    }

    @Override
    public void postBuild(C container) {
        if(this.headTypeBuilder != null) {
            container.setHeadType(headTypeBuilder.apply(container));
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
