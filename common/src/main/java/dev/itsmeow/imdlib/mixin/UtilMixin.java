package dev.itsmeow.imdlib.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import dev.itsmeow.imdlib.IMDLib;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Util.class)
public abstract class UtilMixin {

    @Inject(at = @At("HEAD"), method = "doFetchChoiceType(Lcom/mojang/datafixers/DSL$TypeReference;Ljava/lang/String;)Lcom/mojang/datafixers/types/Type;", require = 0, cancellable = true)
    private static void doFetchChoiceType(DSL.TypeReference typeReference, String string, CallbackInfoReturnable<Type<?>> cir) {
        if(string != null && IMDLib.getRegistries().isPresent() && string.startsWith(IMDLib.getRegistries().get().getModId())) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

}
