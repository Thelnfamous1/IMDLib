package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public interface IContainerItem<T extends MobEntity & IContainable> {
    ITooltipFunction VARIANT_TOOLTIP = (container, stack, world, tooltip) -> {
        CompoundNBT compoundnbt = stack.getTag();
        if(compoundnbt != null && compoundnbt.contains("BucketVariantTag", Constants.NBT.TAG_STRING)) {
            String id = compoundnbt.getString("BucketVariantTag");
            tooltip.add((new TranslationTextComponent("entity." + container.getModId() + "." + container.getEntityName().toLowerCase() + ".type." + container.getVariantForName(id).getName())).applyTextStyles(TextFormatting.ITALIC, TextFormatting.GRAY));
        }
    };

    static String getVariantIfPresent(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        if(compoundnbt != null && compoundnbt.contains("BucketVariantTag", Constants.NBT.TAG_STRING)) {
            return compoundnbt.getString("BucketVariantTag");
        }
        return "";
    }

    default <A extends Item & IContainerItem<T>> void addPropertyOverrides(A item) {
        item.addPropertyOverride(new ResourceLocation(item.getContainer().getModId(), "variant"), (stack, world, entity) -> {
            String variant = IContainerItem.getVariantIfPresent(stack);
            return !variant.isEmpty() ? item.getContainer().getVariantIndex(item.getContainer().getVariantForName(variant)) + 1 : 0;
        });
    }

    EntityTypeContainer<T> getContainer();

    default EntityType<T> getEntityType() {
        return getContainer().getEntityType();
    }

    default void placeEntity(World worldIn, ItemStack stack, BlockPos pos) {
        T entity = this.getEntityType().spawn(worldIn, stack.getTag(), stack.hasDisplayName() ? stack.getDisplayName() : null, null, pos, SpawnReason.BUCKET, true, false);
        if(entity != null) {
            entity.setFromContainer(true);
            entity.readFromContainer(stack);
            if(stack.getTag() != null) {
                entity.readFromContainerTag(stack.getTag());
            }
        }
    }

    @FunctionalInterface
    interface ITooltipFunction {
        void addInformation(EntityTypeContainerContainable<? extends MobEntity, ?> container, ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip);
    }
}
