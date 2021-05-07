package dev.itsmeow.imdlib.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.TypeWrapper;
import dev.itsmeow.imdlib.entity.util.builder.IEntityBuilder;
import dev.itsmeow.imdlib.entity.util.variant.EntityVariant;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.BiomeListBuilder;
import dev.itsmeow.imdlib.util.HeadType;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractEntityBuilder<T extends Mob, C extends EntityTypeContainer<T>, B extends AbstractEntityBuilder<T, C, B>> implements IEntityBuilder<T, C, B> {
    protected final Class<T> entityClass;
    protected final String entityName;
    protected final EntityType.EntityFactory<T> factory;
    protected final String modid;
    protected final Supplier<AttributeSupplier.Builder> attributeMap;
    public boolean hasSpawns;
    protected MobCategory spawnType;
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
    protected Supplier<Set<ResourceKey<Biome>>> defaultBiomeSupplier;
    protected SpawnPlacements.Type placementType;
    protected Heightmap.Types heightMapType;
    protected SpawnPlacements.SpawnPredicate<T> placementPredicate;
    protected int variantCount = 0;
    protected IVariant[] variants;
    protected boolean hasEgg;
    protected Function<C, HeadType> headTypeBuilder;

    protected AbstractEntityBuilder(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
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
        this.spawnType = MobCategory.CREATURE;
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

    @ExpectPlatform
    protected static Supplier<Set<ResourceKey<Biome>>> toBiomes(TypeWrapper[] biomeTypes, boolean overworldOnly) {
        return () -> Lists.newArrayList(biomeTypes).stream().flatMap(type -> BiomeDictionary.getBiomes(type).stream()).filter(b -> !overworldOnly || BiomeDictionary.hasType(b, BiomeDictionary.Type.OVERWORLD)).collect(Collectors.toSet());
    }

    public abstract B getImplementation();

    @Override
    public B spawn(MobCategory type, int weight, int min, int max) {
        this.hasSpawns = true;
        this.spawnType = type;
        this.spawnWeight = weight;
        this.spawnMinGroup = min;
        this.spawnMaxGroup = max;
        return getImplementation();
    }

    @Override
    public B spawnCosts(double cost, double maxCost) {
        if (!hasSpawns) {
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
        return config(configurationInit, holder -> {
        });
    }

    @Override
    public B clientConfig(CustomConfigurationInit configurationInit) {
        return clientConfig(configurationInit, holder -> {
        });
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
    public B placement(SpawnPlacements.Type type, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<T> predicate) {
        if (!hasSpawns) {
            throw new RuntimeException("You must specify spawns before placement");
        }
        this.placementType = type;
        this.heightMapType = heightMap;
        this.placementPredicate = predicate;
        return getImplementation();
    }

    @Override
    public B defaultPlacement(SpawnPlacements.SpawnPredicate<T> predicate) {
        return placement(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
    }

    @Override
    public B waterPlacement() {
        return placement(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTypeContainer::waterSpawn);
    }

    @Override
    public B waterPlacement(SpawnPlacements.SpawnPredicate<T> predicate) {
        return placement(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
    }

    @Override
    public B biomes(TypeWrapper... biomeTypes) {
        if (!hasSpawns) {
            throw new RuntimeException("You must specify spawns before biomes");
        }
        this.defaultBiomeSupplier = toBiomes(biomeTypes, false);
        return getImplementation();
    }

    @Override
    public B biomesOverworld(TypeWrapper... biomeTypes) {
        if (!hasSpawns) {
            throw new RuntimeException("You must specify spawns before biomes");
        }
        this.defaultBiomeSupplier = toBiomes(biomeTypes, true);
        return getImplementation();
    }

    @Override
    public B biomes(Supplier<ResourceKey<Biome>[]> biomes) {
        if (!hasSpawns) {
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
        for (int i = 0; i < nameTextures.length; i++) {
            String nameTex = nameTextures[i];
            variants[i] = new EntityVariant(modid, nameTex, this.entityName + "_" + nameTex);
        }
        return getImplementation();
    }

    @Override
    public B variants(int max) {
        if (max > 0) {
            this.variantCount = max;
            this.variants = new EntityVariant[max];
            for (int i = 0; i < max; i++) {
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
        for (int i = 0; i < variantCount; i++) {
            variantList[i] = constructor.apply(variants[i]);
        }
        this.variants = variantList;
        return getImplementation();
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
        if (this.headTypeBuilder != null) {
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
