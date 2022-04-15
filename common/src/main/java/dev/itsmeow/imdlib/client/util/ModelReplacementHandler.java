package dev.itsmeow.imdlib.client.util;

import dev.itsmeow.imdlib.client.render.ImplRenderer;
import dev.itsmeow.imdlib.util.SafePlatform;
import dev.itsmeow.imdlib.util.config.ConfigBuilder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModelReplacementHandler {

    public static ModelReplacementHandler INSTANCE = null;

    public final Logger LOG = LogManager.getLogger();
    public final String parent_modid;
    protected Map<Pair<String, String>, Supplier<Supplier<ReplaceDefinition<?>>>> replaceDefs = new HashMap<>();
    protected ReplacementConfig config;

    public ModelReplacementHandler(String modid) {
        this.parent_modid = modid;
        INSTANCE = this;
    }

    public ReplacementConfig getConfig(ConfigBuilder builder) {
        return this.getConfig(builder, (a, b, c) -> {
        });
    }

    public ReplacementConfig getConfig(ConfigBuilder builder, TriConsumer<ConfigBuilder, String, Map<String, Supplier<Boolean>>> manuals) {
        return this.config = new ReplacementConfig(this, builder, manuals);
    }

    public void addReplace(String modid, String name, Supplier<Supplier<ReplaceDefinition<?>>> definition) {
        replaceDefs.put(Pair.of(modid, name), definition);
        LOG.debug(String.format("[%s] Registering replace for %s from %s", parent_modid, name, modid));
    }

    public boolean getEnabledAndLoaded(String mod, String override) {
        Map<String, Supplier<Boolean>> overrides = config.replace_config.getModsMap().get(mod);
        if (overrides == null)
            return false;
        return overrides.containsKey(override) ? overrides.get(override).get() : false;
    }

    public void overwriteRenders(EntityRenderDispatcher dispatcher, Map<EntityType<?>, EntityRenderer<?>> renderers) {
        replaceDefs.forEach((pair, definitionSupplier) -> {
            boolean doReplace = getEnabledAndLoaded(pair.getLeft(), pair.getRight());
            if (SafePlatform.isModLoaded(pair.getLeft())) {
                ReplaceDefinition<LivingEntity> def = (ReplaceDefinition<LivingEntity>) definitionSupplier.get().get();
                if (doReplace) {
                    renderers.put(def.type, def.factory.apply(dispatcher));
                    LOG.debug(String.format("[%s] Overriding %s / %s in %s", parent_modid, pair.getRight(), def.type.getDescription(), pair.getLeft()));
                } else {
                    LOG.debug(String.format("[%s] Was going to override %s / %s in %s, but it is disabled!", parent_modid, pair.getRight(), def.type.getDescription(), pair.getLeft()));
                }
            } else {
                LOG.debug(String.format("[%s] %s was not replaced, because %s is not loaded! Config %s", parent_modid, pair.getRight(), pair.getLeft(), doReplace));
            }
        });
    }

    public <T extends Mob, A extends EntityModel<T>> ReplaceDefinition<T> lambdaReplace(EntityType<T> type, float shadowSize, ImplRenderer.RenderDef<T, A> renderDef) {
        return new ReplaceDefinition<>(type, renderDef.apply(ImplRenderer.factory(parent_modid, shadowSize)).done());
    }

    public static class ReplaceDefinition<T extends LivingEntity> {

        public final EntityType<T> type;
        public final Function<EntityRenderDispatcher, EntityRenderer<T>> factory;

        public ReplaceDefinition(EntityType<T> type, Function<EntityRenderDispatcher, EntityRenderer<T>> factory) {
            this.type = type;
            this.factory = factory;
        }

    }

    public static class ReplacementConfig {

        public OverridesConfiguration replace_config;

        public ReplacementConfig(ModelReplacementHandler parent, ConfigBuilder builder, TriConsumer<ConfigBuilder, String, Map<String, Supplier<Boolean>>> manuals) {
            Map<String, Map<String, Supplier<Supplier<ReplaceDefinition<?>>>>> replaceMap = new HashMap<>();
            for(Pair<String, String> pair : parent.replaceDefs.keySet()) {
                replaceMap.putIfAbsent(pair.getLeft(), new HashMap<>());
                replaceMap.get(pair.getLeft()).put(pair.getRight(), parent.replaceDefs.get(pair));
            }

            Map<String, Map<String, Supplier<Boolean>>> map = new HashMap<>();
            for(String modid : replaceMap.keySet()) {
                map.putIfAbsent(modid, new HashMap<>());
                builder.push(modid);
                for (String name : replaceMap.get(modid).keySet()) {
                    addConfig(builder, map, modid, name);
                }
                manuals.accept(builder, modid, map.get(modid));
                builder.pop();
            }
            replace_config = new OverridesConfiguration(map);
        }

        public static void addConfig(ConfigBuilder builder, Map<String, Map<String, Supplier<Boolean>>> map, String modid, String name) {
            map.get(modid).put(name, builder.define("replace_" + name, true));
        }

        public static class OverridesConfiguration {
            public final Map<String, Map<String, Supplier<Boolean>>> mods;

            public OverridesConfiguration(Map<String, Map<String, Supplier<Boolean>>> mods) {
                this.mods = mods;
            }

            public Map<String, Map<String, Supplier<Boolean>>> getModsMap() {
                return mods;
            }
        }

    }

}
