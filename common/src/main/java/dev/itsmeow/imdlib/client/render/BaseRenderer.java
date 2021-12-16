package dev.itsmeow.imdlib.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class BaseRenderer<T extends Mob, A extends EntityModel<T>> extends MobRenderer<T, A> {

    public BaseRenderer(EntityRendererProvider.Context renderManagerIn, A entityModelIn, float shadowSizeIn) {
        super(renderManagerIn, entityModelIn, shadowSizeIn);
    }

    public BaseRenderer<T, A> layer(Function<BaseRenderer<T, A>, RenderLayer<T, A>> layer) {
        this.addLayer(layer.apply(this));
        return this;
    }

    public BaseRenderer<T, A> layers(ArrayList<Function<BaseRenderer<T, A>, RenderLayer<T, A>>> layers) {
        layers.forEach(layer -> this.addLayer(layer.apply(this)));
        return this;
    }

}
