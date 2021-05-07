package dev.itsmeow.imdlib.client.render;

import me.shedaniel.architectury.registry.entity.EntityRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ItemSupplier;

import java.util.function.Function;

public class RenderFactory {

    public final String modid;

    public RenderFactory(String modid) {
        this.modid = modid;
    }

    public static <T extends Entity> void addRender(EntityType<T> clazz, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer) {
        EntityRenderers.register(clazz, renderer);
    }

    public static <T extends Entity & ItemSupplier> Function<EntityRenderDispatcher, EntityRenderer<T>> sprite() {
        return mgr -> new ThrownItemRenderer<>(mgr, Minecraft.getInstance().getItemRenderer());
    }

    public static <T extends Entity> Function<EntityRenderDispatcher, EntityRenderer<T>> nothing() {
        return r -> new EntityRenderer<T>(r) {
            @Override
            public ResourceLocation getTextureLocation(T entity) {
                return null;
            }
        };
    }

    public <T extends Mob, M extends EntityModel<T>> ImplRenderer.Builder<T, M> r(float shadowSize) {
        return ImplRenderer.factory(modid, shadowSize);
    }

    public <T extends Mob, M extends EntityModel<T>> void addRender(EntityType<T> clazz, float shadowSize, Function<ImplRenderer.Builder<T, M>, ImplRenderer.Builder<T, M>> render) {
        EntityRenderers.register(clazz, render.apply(this.r(shadowSize)).done());
    }

}
