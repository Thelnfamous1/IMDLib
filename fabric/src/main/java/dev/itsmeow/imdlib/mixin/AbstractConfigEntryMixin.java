package dev.itsmeow.imdlib.mixin;

import dev.itsmeow.imdlib.util.SafePlatform;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(AbstractConfigEntry.class)
public abstract class AbstractConfigEntryMixin {

    @Unique
    private static Map<Component, Component> componentMap = new WeakHashMap<>();

    @Shadow
    public abstract Component getFieldName();

    @Inject(at = @At("HEAD"), method = "getDisplayedFieldName()Lnet/minecraft/network/chat/Component;", cancellable = true)
    private void getDisplayedFieldName(CallbackInfoReturnable<Component> callbackInfoReturnable) {
        Component field = getFieldName();
        if(componentMap.containsKey(field)) {
            callbackInfoReturnable.setReturnValue(componentMap.get(field));
            callbackInfoReturnable.cancel();
        } else {
            String fieldName = field.getString();
            String modId = SafePlatform.modId();
            if (fieldName.matches("config\\." + modId + "\\.([\\s\\S]+?-)?" + modId + "-(server-default|client|server|common)\\.entities\\.\\w+$")) {
                fieldName = fieldName.replaceFirst("config\\." + modId + "\\.([\\s\\S]+?-)?" + modId + "-(server-default|client|server|common)\\.entities", "");
                Component c = Component.translatable("entity." + modId + fieldName);
                componentMap.put(field, c);
                callbackInfoReturnable.setReturnValue(c);
                callbackInfoReturnable.cancel();
            } else if (fieldName.matches("config\\." + modId + "\\.([\\s\\S]+?-)?" + modId + "-(server-default|client|server|common)\\.\\w+$")) {
                fieldName = fieldName.replaceFirst("config\\." + modId + "\\.([\\s\\S]+?-)?" + modId + "-(server-default|client|server|common)", "");
                Component c = Component.translatable("config." + modId + fieldName);
                componentMap.put(field, c);
                callbackInfoReturnable.setReturnValue(c);
                callbackInfoReturnable.cancel();
            } else if (fieldName.matches("config\\." + modId + "\\.([\\s\\S]+?-)?" + modId + "-(server-default|client|server|common)\\.entities\\.\\w+\\.(\\w+\\.?)+$")) {
                fieldName = fieldName.replaceFirst("config\\." + modId + "\\.([\\s\\S]+?-)?" + modId + "-(server-default|client|server|common)\\.entities\\.\\w+", "");
                Component c = Component.translatable("config." + modId + fieldName);
                componentMap.put(field, c);
                callbackInfoReturnable.setReturnValue(c);
                callbackInfoReturnable.cancel();
            }
        }
    }

}
