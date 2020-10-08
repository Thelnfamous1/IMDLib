package dev.itsmeow.imdlib.item;

import java.util.List;

import javax.annotation.Nullable;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.util.IContainable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
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

public interface IContainerItem<T extends MobEntity & IContainable> {
    public static final ITooltipFunction VARIANT_TOOLTIP = (container, stack, world, tooltip) -> {
        CompoundNBT compoundnbt = stack.getTag();
        if(compoundnbt != null && compoundnbt.contains("BucketVariantTag", Constants.NBT.TAG_STRING)) {
            String id = compoundnbt.getString("BucketVariantTag");
            TextFormatting[] atextformatting = new TextFormatting[] { TextFormatting.ITALIC, TextFormatting.GRAY };
            tooltip.add((new TranslationTextComponent("entity.betteranimalsplus." + container.entityName.toLowerCase() + ".type." + container.getVariantForName(id).getName())).applyTextStyles(atextformatting));
        }
    };

    public static String getVariantIfPresent(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        if(compoundnbt != null && compoundnbt.contains("BucketVariantTag", Constants.NBT.TAG_STRING)) {
            return compoundnbt.getString("BucketVariantTag");
        }
        return "";
    }

    default <A extends Item & IContainerItem<T>> void addPropertyOverrides(A item) {
        item.addPropertyOverride(new ResourceLocation(item.getContainer().getModId(), "variant"), new IItemPropertyGetter() {
            @SuppressWarnings("deprecation")
            @Override
            public float call(ItemStack stack, World world, LivingEntity entity) {
                String variant = IContainerItem.getVariantIfPresent(stack);
                return variant != null && !variant.isEmpty() ? item.getContainer().getVariantIndex(item.getContainer().getVariantForName(variant)) + 1 : 0;
            }
        });
    }

    public EntityTypeContainer<T> getContainer();

    default EntityType<T> getEntityType() {
        return getContainer().entityType;
    }

    default void placeEntity(World worldIn, ItemStack stack, BlockPos pos) {
        T entity = this.getEntityType().spawn(worldIn, stack.getTag(), stack.hasDisplayName() ? stack.getDisplayName() : null, (PlayerEntity) null, pos, SpawnReason.BUCKET, true, false);
        if(entity != null) {
            entity.setFromContainer(true);
            entity.readFromContainer(stack);
            if(stack.getTag() != null) {
                entity.readFromContainerTag(stack.getTag());
            }
        }
    }

    @FunctionalInterface
    public static interface ITooltipFunction {
        void addInformation(EntityTypeContainerContainable<? extends MobEntity, ?> container, ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip);
    }
}
