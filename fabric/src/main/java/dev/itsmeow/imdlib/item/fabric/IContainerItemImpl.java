package dev.itsmeow.imdlib.item.fabric;

import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.item.IContainerItem;
import dev.itsmeow.imdlib.mixin.ItemPropertiesInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

public interface IContainerItemImpl<T extends Mob & IContainable> {

    @Environment(EnvType.CLIENT)
    default <A extends Item & IContainerItem<T>> void addPropertyOverrides(A item) {
        ItemPropertiesInvoker.invokeRegister(item, new ResourceLocation(item.getContainer().getModId(), "variant"), (stack, world, entity, i) -> {
            String variant = IContainerItem.getVariantIfPresent(stack);
            return !variant.isEmpty() ? item.getContainer().getVariantIndex(item.getContainer().getVariantForName(variant)) + 1 : 0;
        });
    }

}
