package dev.itsmeow.imdlib.client.render;

import dev.architectury.registry.level.entity.EntityRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ItemSupplier;

import java.util.function.Function;
import java.util.function.Supplier;

public record RenderFactory(String modid) {

    public static <T extends Entity> void addRender(Supplier<EntityType<? extends T>> type, EntityRendererProvider renderer) {
        EntityRendererRegistry.register(type, renderer);
    }

    public static <T extends Entity & ItemSupplier> EntityRendererProvider<T> sprite() {
        return ThrownItemRenderer::new;
    }

    public static <T extends Entity> EntityRendererProvider<T> nothing() {
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

    public <T extends Mob, M extends EntityModel<T>> void addRender(Supplier<EntityType<? extends T>> type, float shadowSize, Function<ImplRenderer.Builder<T, M>, ImplRenderer.Builder<T, M>> render) {
        EntityRendererRegistry.register(type, render.apply(this.r(shadowSize)).done());
    }

}
