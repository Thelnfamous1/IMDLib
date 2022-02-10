package dev.itsmeow.imdlib.mixin;

import dev.itsmeow.imdlib.client.util.ModelReplacementHandler;
import dev.itsmeow.imdlib.util.config.CommonConfigAPI;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = EntityRenderDispatcher.class, priority = 2022) // lower priority so we go last
public class EntityRenderDispatcherMixin {

    @Shadow
    @Final
    private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Inject(method = "registerRenderers", at = @At(value = "TAIL"))
    public void onRegisterRenderers(ItemRenderer itemRenderer, ReloadableResourceManager manager, CallbackInfo info) {
        CommonConfigAPI.loadClientReplace();
        ModelReplacementHandler.INSTANCE.overwriteRenders((EntityRenderDispatcher) (Object) this, renderers);
    }

}
