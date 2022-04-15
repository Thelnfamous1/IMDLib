package dev.itsmeow.imdlib.mixin;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {

    @Accessor("renderers")
    Map<EntityType<?>, EntityRenderer<?>> getRenderers();

}
