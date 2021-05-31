package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;

public class ModSpawnEggItem extends SpawnEggItem {

    protected static final DefaultDispenseItemBehavior EGG_DISPENSE_ACTION = new DefaultDispenseItemBehavior() {
        public ItemStack dispenseStack(BlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> entitytype = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
            entitytype.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            stack.shrink(1);
            return stack;
        }
    };
    private final EntityType<?> type;
    private final String modid;
    public final String name;

    public ModSpawnEggItem(EntityTypeContainer<?> container) {
        super(container.getEntityType(), container.getDefinition().getEggSolidColor(), container.getDefinition().getEggSpotColor(), new Properties().tab(CreativeModeTab.TAB_MISC));
        this.type = container.getEntityType();
        this.modid = container.getModId();
        this.name = container.getEntityName().toLowerCase() + "_spawn_egg";
        // TODO this.setRegistryName(container.getModId(), name + "_spawn_egg");
        DispenserBlock.registerBehavior(this, EGG_DISPENSE_ACTION);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (type != null) {
            ResourceLocation eh = new ResourceLocation(this.type.getDescriptionId());
            return "entity." + modid + "." + eh.getPath();
        }
        return "item." + modid + ".emptyegg";
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent("misc." + modid + ".eggorder",
                new TranslatableComponent(this.getDescriptionId(stack)));
    }

    @Override
    public EntityType<?> getType(CompoundTag tag) {
        return this.type;
    }

    @Override
    public boolean spawnsEntity(CompoundTag tag, EntityType<?> type) {
        return type == this.type;
    }

}
