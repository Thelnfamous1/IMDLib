package dev.itsmeow.imdlib.client.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.itsmeow.imdlib.client.render.ImplRenderer;
import dev.itsmeow.imdlib.client.render.ImplRenderer.RenderDef;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModelReplacementHandler {

    public final Logger LOG = LogManager.getLogger();
    public final String parent_modid;
    protected Map<RegistrationTime, Multimap<Pair<String, String>, Supplier<Supplier<ReplaceDefinition<?>>>>> replaceDefs = new HashMap<>();
    protected Map<RegistrationTime, Multimap<String, Supplier<Runnable>>> modActions = new HashMap<>();
    protected ReplacementConfig config;

    public ModelReplacementHandler(String modid) {
        this.parent_modid = modid;
    }

    public ReplacementConfig getConfig(ForgeConfigSpec.Builder builder) {
        return this.getConfig(builder, (a, b) -> {
        });
    }

    public ReplacementConfig getConfig(ForgeConfigSpec.Builder builder, BiConsumer<ForgeConfigSpec.Builder, Map<String, Map<String, ForgeConfigSpec.BooleanValue>>> manuals) {
        return this.config = new ReplacementConfig(this, builder, manuals);
    }

    public void mre() {
        runActions(RegistrationTime.MODELREGISTRY);
        overwriteRenders(RegistrationTime.MODELREGISTRY);
    }

    public void clientSetup() {
        runActions(RegistrationTime.CLIENTSETUP);
        overwriteRenders(RegistrationTime.CLIENTSETUP);
    }

    public void addReplace(RegistrationTime time, String modid, String name, Supplier<Supplier<ReplaceDefinition<?>>> definition) {
        replaceDefs.putIfAbsent(time, MultimapBuilder.hashKeys().linkedHashSetValues().build());
        replaceDefs.get(time).put(Pair.of(modid, name), definition);
        LOG.debug(String.format("[%s] Registering replace for %s from %s at %s", parent_modid, name, modid, time.name()));
    }

    public void addAction(RegistrationTime time, String modid, Supplier<Runnable> action) {
        modActions.putIfAbsent(time, MultimapBuilder.hashKeys().linkedHashSetValues().build());
        modActions.get(time).put(modid, action);
        LOG.debug(String.format("[%s] Registering action for %s at %s", parent_modid, modid, time.name()));
    }

    public boolean getEnabledAndLoaded(String mod, String override) {
        Map<String, ForgeConfigSpec.BooleanValue> overrides = config.replace_config.getModsMap().get(mod);
        if (overrides == null)
            return false;
        return overrides.containsKey(override) ? overrides.get(override).get() : false;
    }

    protected void overwriteRenders(RegistrationTime phase) {
        replaceDefs.putIfAbsent(phase, MultimapBuilder.hashKeys().hashSetValues().build());
        replaceDefs.get(phase).forEach((pair, definitionSupplier) -> {
            boolean doReplace = getEnabledAndLoaded(pair.getLeft(), pair.getRight());
            if (ModList.get().isLoaded(pair.getLeft()) || pair.getLeft().equals("minecraft")) {
                ReplaceDefinition<?> def = definitionSupplier.get().get();
                if (doReplace) {
                    IRenderFactory<LivingEntity> factory = manager -> (EntityRenderer<LivingEntity>) def.factory.createRenderFor(manager);
                    RenderingRegistry.registerEntityRenderingHandler(def.type, factory);
                    LOG.debug(String.format("[%s] Overriding %s / %s in %s", parent_modid, pair.getRight(), def.type.getDescription(), pair.getLeft()));
                } else {
                    LOG.debug(String.format("[%s] Was going to override %s / %s in %s, but it is disabled!", parent_modid, pair.getRight(), def.type.getDescription(), pair.getLeft()));
                }
            } else {
                LOG.debug(String.format("[%s] %s was not replaced, because %s is not loaded! Config %s", parent_modid, pair.getRight(), pair.getLeft(), doReplace));
            }
        });
    }

    protected void runActions(RegistrationTime phase) {
        modActions.putIfAbsent(phase, MultimapBuilder.hashKeys().hashSetValues().build());
        modActions.get(phase).forEach((modid, action) -> {
            if (ModList.get().isLoaded(modid) || modid.equals("minecraft")) {
                action.get().run();
                LOG.debug(String.format("[%s] Running action for %s", parent_modid, modid));
            } else {
                LOG.debug(String.format("[%s] No action executed for %s, as it is not loaded.", parent_modid, modid));
            }
        });
    }

    public <T extends Mob, A extends EntityModel<T>> ReplaceDefinition<T> lambdaReplace(EntityType<T> type, float shadowSize, RenderDef<T, A> renderDef) {
        return new ReplaceDefinition<>(type, renderDef.apply(ImplRenderer.factory(parent_modid, shadowSize)).done());
    }

    public enum RegistrationTime {
        MODELREGISTRY,
        CLIENTSETUP
    }

    public static class ReplaceDefinition<T extends LivingEntity> {

        public final EntityType<T> type;
        public final IRenderFactory<T> factory;

        public ReplaceDefinition(EntityType<T> type, IRenderFactory<T> factory) {
            this.type = type;
            this.factory = factory;
        }

    }

    public static class ReplacementConfig {

        public OverridesConfiguration replace_config;

        public ReplacementConfig(ModelReplacementHandler parent, ForgeConfigSpec.Builder builder, BiConsumer<ForgeConfigSpec.Builder, Map<String, Map<String, ForgeConfigSpec.BooleanValue>>> manuals) {
            Map<String, Map<String, ForgeConfigSpec.BooleanValue>> map = new HashMap<>();
            parent.replaceDefs.values().forEach(m -> m.keySet().forEach(pair -> addConfig(builder, map, pair.getLeft(), pair.getRight())));
            manuals.accept(builder, map);
            replace_config = new OverridesConfiguration(map);
        }

        public static void addConfig(ForgeConfigSpec.Builder builder, Map<String, Map<String, ForgeConfigSpec.BooleanValue>> map, String modid, String name) {
            map.putIfAbsent(modid, new HashMap<>());
            builder.push(modid);
            ForgeConfigSpec.BooleanValue value = builder.define("replace_" + name, true);
            map.get(modid).put(name, value);
            builder.pop();
        }

        public static class OverridesConfiguration {
            public final Map<String, Map<String, ForgeConfigSpec.BooleanValue>> mods;

            public OverridesConfiguration(Map<String, Map<String, ForgeConfigSpec.BooleanValue>> mods) {
                this.mods = mods;
            }

            public Map<String, Map<String, ForgeConfigSpec.BooleanValue>> getModsMap() {
                return mods;
            }
        }

    }

}
