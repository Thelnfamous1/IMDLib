package dev.itsmeow.imdlib.item;

import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainerContainable;
import dev.itsmeow.imdlib.entity.interfaces.IContainable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemModEntityContainer<T extends MobEntity & IContainable> extends Item implements IContainerItem<T> {

    protected final EntityTypeContainerContainable<T, ItemModEntityContainer<T>> typeContainer;
    protected final ITooltipFunction tooltip;

    public static <T extends MobEntity & IContainable> BiFunction<EntityTypeContainerContainable<T, ItemModEntityContainer<T>>, ITooltipFunction, ItemModEntityContainer<T>> get(String name, ItemGroup group) {
        return (container, tooltip) -> new ItemModEntityContainer<>(container, String.format(name, container.getEntityName()), tooltip, group);
    }

    public ItemModEntityContainer(EntityTypeContainerContainable<T, ItemModEntityContainer<T>> typeContainer, String name, ItemGroup group) {
        this(typeContainer, name, IContainerItem.VARIANT_TOOLTIP, group);
    }

    public ItemModEntityContainer(EntityTypeContainerContainable<T, ItemModEntityContainer<T>> typeContainer, String name, ITooltipFunction tooltip, ItemGroup group) {
        super(new Item.Properties().maxStackSize(1).group(group));
        this.setRegistryName(typeContainer.getModId(), name);
        this.typeContainer = typeContainer;
        this.tooltip = tooltip;
        this.addPropertyOverrides(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
            if(raytraceresult.getType() == RayTraceResult.Type.MISS) {
                return new ActionResult<>(ActionResultType.PASS, itemstack);
            } else if(raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(ActionResultType.PASS, itemstack);
            } else {
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
                BlockPos blockpos = blockraytraceresult.getPos();
                this.placeEntity(worldIn, playerIn.getHeldItem(handIn), blockpos.offset(blockraytraceresult.getFace()));
                if(!playerIn.isCreative()) {
                    playerIn.setItemStackToSlot(handIn == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, new ItemStack(this.typeContainer.getEmptyContainerItem()));
                }
                return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(tooltip != null) {
            this.tooltip.addInformation(this.typeContainer, stack, worldIn, tooltip);
        }
    }

    @Override
    public EntityTypeContainer<T> getContainer() {
        return typeContainer;
    }

}