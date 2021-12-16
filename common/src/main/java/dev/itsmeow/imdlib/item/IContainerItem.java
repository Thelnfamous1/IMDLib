package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.mixin.ItemPropertiesInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IContainerItem<T extends Mob & IContainable> {
    ITooltipFunction VARIANT_TOOLTIP = (container, stack, world, tooltip) -> {
        CompoundTag compoundnbt = stack.getTag();
        if (compoundnbt != null && compoundnbt.contains("BucketVariantTag", 8)) {
            String id = compoundnbt.getString("BucketVariantTag");
            tooltip.add((new TranslatableComponent("entity." + container.getModId() + "." + container.getEntityName().toLowerCase() + ".type." + container.getVariantForName(id).getName())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    };

    static String getVariantIfPresent(ItemStack stack) {
        CompoundTag compoundnbt = stack.getTag();
        if (compoundnbt != null && compoundnbt.contains("BucketVariantTag", 8)) {
            return compoundnbt.getString("BucketVariantTag");
        }
        return "";
    }

    @SuppressWarnings("deprecation")
    @Environment(EnvType.CLIENT)
    default <A extends Item & IContainerItem<T>> void addPropertyOverrides(A item) {
        ItemPropertiesInvoker.invokeRegister(item, new ResourceLocation(item.getContainer().getModId(), "variant"), (stack, world, entity, i) -> {
            String variant = IContainerItem.getVariantIfPresent(stack);
            return !variant.isEmpty() ? item.getContainer().getVariantIndex(item.getContainer().getVariantForName(variant)) + 1 : 0;
        });
    }

    EntityTypeContainer<T> getContainer();

    default EntityType<T> getEntityType() {
        return getContainer().getEntityType();
    }

    default void placeEntity(ServerLevel worldIn, ItemStack stack, BlockPos pos) {
        T entity = this.getEntityType().spawn(worldIn, stack.getTag(), stack.hasCustomHoverName() ? stack.getDisplayName() : null, null, pos, MobSpawnType.BUCKET, true, false);
        if (entity != null) {
            entity.setFromContainer(true);
            entity.readFromContainer(stack);
            if (stack.getTag() != null) {
                entity.readFromContainerTag(stack.getTag());
            }
        }
    }

    @FunctionalInterface
    interface ITooltipFunction {
        void addInformation(EntityTypeContainerContainable<? extends Mob, ?> container, ItemStack stack, @Nullable Level worldIn, List<Component> tooltip);
    }
}
