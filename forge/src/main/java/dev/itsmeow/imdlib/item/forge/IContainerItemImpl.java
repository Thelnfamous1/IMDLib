package dev.itsmeow.imdlib.item.forge;

import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.item.IContainerItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public interface IContainerItemImpl<T extends Mob & IContainable> {

    @OnlyIn(Dist.CLIENT)
    default <A extends Item & IContainerItem<T>> void addPropertyOverrides(A item) {
        ItemProperties.register(item, new ResourceLocation(item.getContainer().getModId(), "variant"), (stack, world, entity, i) -> {
            String variant = IContainerItem.getVariantIfPresent(stack);
            if(!variant.isEmpty()) {
                Optional<IVariant> variantO = item.getContainer().getVariantForName(variant);
                return variantO.isPresent() ? item.getContainer().getVariantIndex(variantO.get()) + 1 : 0;
            }
            return 0;
        });
    }

}
