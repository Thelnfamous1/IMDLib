package dev.itsmeow.imdlib.item.forge;

import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.item.IContainerItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IContainerItemImpl<T extends Mob & IContainable> {

    @OnlyIn(Dist.CLIENT)
    default <A extends Item & IContainerItem<T>> void addPropertyOverrides(A item) {
        ItemProperties.register(item, new ResourceLocation(item.getContainer().getModId(), "variant"), (stack, world, entity, i) -> {
            String variant = IContainerItem.getVariantIfPresent(stack);
            return !variant.isEmpty() ? item.getContainer().getVariantIndex(item.getContainer().getVariantForName(variant)) + 1 : 0;
        });
    }

}
