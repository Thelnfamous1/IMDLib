package dev.itsmeow.imdlib.config;

import dev.itsmeow.imdlib.entity.AbstractEntityBuilder;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.EntityTypeDefinition;
import dev.itsmeow.imdlib.entity.interfaces.ISelectiveVariantTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeEntityTypeContainerConfigHandler implements EntityTypeContainerConfigHandler{
    public EntityTypeContainer<?> container;
    public EntityConfiguration config;

    public ForgeEntityTypeContainerConfigHandler(EntityTypeContainer<?> container) {
        this.container = container;
    }

    //move configuration to everything down here
    protected void createConfiguration(ForgeConfigSpec.Builder builder) {
        config = new EntityConfiguration(builder);
    }

    protected void customConfigurationLoad() {
        if (container.getDefinition().getCustomConfigLoad() != null) {
            container.getDefinition().getCustomConfigLoad().load(customConfigHolder);
        }
    }

    protected void clientCustomConfigurationLoad() {
        if (definition.getCustomClientConfigLoad() != null) {
            definition.getCustomClientConfigLoad().load(customConfigHolderClient);
        }
    }

    protected void customConfigurationInit(ForgeConfigSpec.Builder builder) {
        if (definition.getCustomConfigInit() != null) {
            definition.getCustomConfigInit().init(customConfigHolder, builder);
        }
    }

    protected void clientCustomConfigurationInit(ForgeConfigSpec.Builder builder) {
        if (definition.getCustomClientConfigInit() != null) {
            builder.push(this.getEntityName());
            definition.getCustomClientConfigInit().init(customConfigHolderClient, builder);
            builder.pop();
        }
    }
    @FunctionalInterface
    public interface CustomConfigurationLoad {
        void load(CustomConfigurationHolder<?> holder);
    }

    @FunctionalInterface
    public interface CustomConfigurationInit {
        void init(CustomConfigurationHolder<?> holder, ForgeConfigSpec.Builder builder);
    }

    /* Subclasses */
    public static class Builder<T extends Mob> extends AbstractEntityBuilder<T, EntityTypeContainer<T>, Builder<T>> {

        private Builder(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public static <T extends Mob> Builder<T> create(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            return new Builder<>(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public EntityTypeContainer<T> rawBuild() {
            return new EntityTypeContainer<>(new EntityTypeDefinition<>(this));
        }

        @Override
        public Builder<T> getImplementation() {
            return this;
        }

    }

    public static class CustomConfigurationHolder<T extends Mob> {
        protected Map<String, ForgeConfigSpec.ConfigValue<?>> values = new HashMap<>();
        protected EntityTypeContainer<T> container;

        CustomConfigurationHolder(EntityTypeContainer<T> container) {
            this.container = container;
        }

        public int getInt(String path) {
            return getIntHolder(path).get();
        }

        public String getString(String path) {
            return getStringHolder(path).get();
        }

        public double getDouble(String path) {
            return getDoubleHolder(path).get();
        }

        public long getLong(String path) {
            return getLongHolder(path).get();
        }

        public boolean getBoolean(String path) {
            return getBooleanHolder(path).get();
        }

        @SuppressWarnings("unchecked")
        public <V> ForgeConfigSpec.ConfigValue<V> getAnyHolder(String path) {
            return (ForgeConfigSpec.ConfigValue<V>) values.get(path);
        }

        @SuppressWarnings("unchecked")
        public ForgeConfigSpec.ConfigValue<List<String>> getStringListHolder(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v.get() instanceof List<?> ? (ForgeConfigSpec.ConfigValue<List<String>>) v : null;
        }

        public ForgeConfigSpec.IntValue getIntHolder(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.IntValue ? (ForgeConfigSpec.IntValue) v : null;
        }

        @SuppressWarnings("unchecked")
        public ForgeConfigSpec.ConfigValue<String> getStringHolder(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v.get() instanceof String ? (ForgeConfigSpec.ConfigValue<String>) v : null;
        }

        public ForgeConfigSpec.DoubleValue getDoubleHolder(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.DoubleValue ? (ForgeConfigSpec.DoubleValue) v : null;
        }

        public ForgeConfigSpec.LongValue getLongHolder(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.LongValue ? (ForgeConfigSpec.LongValue) v : null;
        }

        public ForgeConfigSpec.BooleanValue getBooleanHolder(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.BooleanValue ? (ForgeConfigSpec.BooleanValue) v : null;
        }

        public void put(ForgeConfigSpec.ConfigValue<?> value) {
            String path = String.join("/", value.getPath().toArray(new String[0]));
            path = path.substring(("entities/" + container.getEntityName() + "/").length());
            values.put(path, value);
        }
    }

    public class EntityConfiguration {
        public ForgeConfigSpec.BooleanValue doSpawning;
        public ForgeConfigSpec.BooleanValue biomeVariants;
        public ForgeConfigSpec.IntValue spawnMinGroup;
        public ForgeConfigSpec.IntValue spawnMaxGroup;
        public ForgeConfigSpec.IntValue spawnWeight;
        public ForgeConfigSpec.BooleanValue useSpawnCosts;
        public ForgeConfigSpec.DoubleValue spawnCostPer;
        public ForgeConfigSpec.DoubleValue spawnMaxCost;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> biomesList;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> spawnCostBiomes;
        public ForgeConfigSpec.BooleanValue doDespawn;

        protected EntityConfiguration(ForgeConfigSpec.Builder builder) {
            EntityTypeContainer<T> container = EntityTypeContainer.this;
            builder.push(container.getEntityName());
            {
                if (definition.getSpawnClassification() == EntityClassification.CREATURE)
                    doDespawn = builder.comment("Allows the entity to despawn freely when no players are nearby, like vanilla monsters do").worldRestart().define("can_despawn", definition.despawns());
                if (hasSpawns()) {
                    builder.push("spawning");
                    {
                        doSpawning = builder.comment("Enables natural spawning - More info on these options: https://minecraft.fandom.com/wiki/Spawn#Java_Edition").worldRestart().define("spawn_naturally", true);
                        spawnWeight = builder.comment("The spawn weight compared to other entities (typically between 6-20)").worldRestart().defineInRange("spawn_weight", definition.getSpawnWeight(), 1, 9999);
                        spawnMinGroup = builder.comment("Minimum amount of entities in spawned groups").worldRestart().defineInRange("minimum_group_size", definition.getSpawnMinGroup(), 1, 9999);
                        spawnMaxGroup = builder.comment("Maximum amount of entities in spawned groups - Must be greater or equal to min value").worldRestart().defineInRange("maximum_group_size", definition.getSpawnMaxGroup(), 1, 9999);
                        biomesList = builder.comment("Enter biome IDs. Supports modded biomes https://minecraft.fandom.com/wiki/Biome#Biome_IDs").worldRestart().defineList("spawn_biomes", setBiomesToIDs(definition.getDefaultSpawnBiomes().get()), input -> input instanceof String);
                        if (ISelectiveVariantTypes.class.isAssignableFrom(getEntityClass())) {
                            biomeVariants = builder.comment("Enables biome based variant selection. This will make this entity choose variants tailored to the biome they spawn in (Only applies to natural spawns)").worldRestart().define("biome_based_variants", true);
                        }
                        builder.push("spawn_costs");
                        {
                            useSpawnCosts = builder.comment("Whether to use spawn costs in spawning or not").worldRestart().define("use_spawn_costs", definition.useSpawnCosts());
                            spawnCostPer = builder.comment("Cost to spawning algorithm per entity spawned").worldRestart().defineInRange("cost_per_spawn", definition.getSpawnCostPer(), Double.MIN_VALUE, Double.MAX_VALUE);
                            spawnMaxCost = builder.comment("Maximum cost the spawning algorithm can accrue for this entity").worldRestart().defineInRange("maximum_cost_per_biome", definition.getSpawnMaxCost(), Double.MIN_VALUE, Double.MAX_VALUE);
                            spawnCostBiomes = builder.comment("Enter biome IDs to use these costs in. Supports modded biomes. An empty list will use spawn_biomes https://minecraft.fandom.com/wiki/Biome#Biome_IDs").worldRestart().defineList("spawn_cost_biomes", new ArrayList<>(), input -> input instanceof String);
                        }
                        builder.pop();
                    }
                    builder.pop();
                }
                EntityTypeContainer.this.customConfigurationInit(builder);
            }
            builder.pop();
        }

        protected void load() {
            if (hasSpawns()) {
                despawn = definition.getSpawnClassification() == EntityClassification.CREATURE ? doDespawn.get() : definition.despawns();
                Function<ForgeConfigSpec.ConfigValue<List<? extends String>>, Set<ResourceKey<Biome>>> biomesLoader = (configList) -> {
                    HashSet<ResourceKey<Biome>> biomeKeys = new HashSet<>();
                    for (String biomeName : configList.get()) {
                        try {
                            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
                            if (biome == null || biome.getRegistryName() == null) {
                                LogManager.getLogger().error("Invalid biome \"" + biomeName + "\" for entity " + getEntityName() + ". No biome exists with that name. Skipping.");
                            } else {
                                biomeKeys.add(ResourceKey.getOrCreateKey(Registry.BIOME_KEY, biome.getRegistryName()));
                            }
                        } catch (Exception e) {
                            LogManager.getLogger().error("Invalid biome name: \"" + biomeName + "\" for entity " + getEntityName() + ". Is it formatted correctly? Skipping.");
                        }
                    }
                    return biomeKeys;
                };

                EntityTypeContainer.this.setSpawnBiomesSupplier(() -> biomesLoader.apply(biomesList));
                if (useSpawnCosts.get()) {
                    EntityTypeContainer.this.setSpawnCostBiomesSupplier(spawnCostBiomes.get().size() == 0 ? spawnBiomesSupplier : () -> biomesLoader.apply(spawnCostBiomes));
                }
            }
            EntityTypeContainer.this.customConfigurationLoad();
        }
    }
}
