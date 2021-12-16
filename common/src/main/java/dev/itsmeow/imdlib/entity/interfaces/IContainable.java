package dev.itsmeow.imdlib.entity.interfaces;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IContainable {

    Mob getImplementation();

    EntityTypeContainerContainable<?, ?> getContainableContainer();

    default void setContainerData(ItemStack container) {
        if (this.getImplementation().hasCustomName()) {
            container.setHoverName(this.getImplementation().getCustomName());
        }
    }

    default void writeFromContainerToEntity(CompoundTag compound) {
        compound.putBoolean("FromBucket", this.isFromContainer());
    }

    default void readFromContainerToEntity(CompoundTag compound) {
        this.setFromContainer(compound.getBoolean("FromBucket"));
    }

    default ItemStack getContainerItem() {
        return new ItemStack(getContainableContainer().getContainerItem());
    }

    default Item getEmptyContainerItem() {
        return getContainableContainer().getEmptyContainerItem();
    }

    default void registerFromContainerKey() {
        this.getImplementation().getEntityData().define(getContainableContainer().getFromContainerDataKey(), false);
    }

    default boolean isFromContainer() {
        return this.getImplementation().getEntityData().get(getContainableContainer().getFromContainerDataKey());
    }

    default void setFromContainer(boolean fromContainer) {
        this.getImplementation().getEntityData().set(getContainableContainer().getFromContainerDataKey(), fromContainer);
    }

    default void readFromContainer(ItemStack stack) {

    }

    default void readFromContainerTag(CompoundTag tag) {

    }

    default boolean processContainerInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == getEmptyContainerItem() && this.getImplementation().isAlive()) {
            itemstack.shrink(1);
            ItemStack itemstack1 = this.getContainerItem();
            this.setContainerData(itemstack1);
            this.onPickupSuccess(player, hand, itemstack1);
            if (itemstack.isEmpty()) {
                player.setItemInHand(hand, itemstack1);
            } else if (!player.getInventory().add(itemstack1)) {
                player.drop(itemstack1, false);
            }
            this.getImplementation().discard();
            return true;
        } else {
            return false;
        }
    }

    default void onPickupSuccess(Player player, InteractionHand hand, ItemStack stack) {

    }

}
