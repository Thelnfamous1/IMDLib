package dev.itsmeow.imdlib.tileentity;

import dev.itsmeow.imdlib.block.BlockAnimalSkull;
import dev.itsmeow.imdlib.entity.util.IVariant;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class TileEntityHead extends TileEntity {

    protected static final TileEntityType<TileEntityHead> HEAD_TYPE = TileEntityType.Builder.create(TileEntityHead::new, HeadType.getAllBlocks()).build(null);

    public static void registerType(RegistryEvent.Register<TileEntityType<?>> event, String modid) {
        HEAD_TYPE.setRegistryName(modid, "head");
        event.getRegistry().register(HEAD_TYPE);
    }

    private HeadType cachedType = null;
    private IVariant cachedVariant = null;

    public TileEntityHead() {
        super(HEAD_TYPE);
    }

    public TileEntityHead(HeadType type) {
        super(HEAD_TYPE);
    }

    @OnlyIn(Dist.CLIENT)
    public EntityModel<? extends Entity> getNewModel() {
        return this.getHeadType().getModelSupplier().get().get();
    }

    private Block getBlock() {
        return this.getBlockState().getBlock();
    }

    public HeadType getHeadType() {
        if(cachedType == null) {
            cachedType = HeadType.valueOf(this.getBlock());
        }
        return cachedType;
    }

    public ResourceLocation getTexture() {
        return this.getHeadVariant().getTexture(null);
    }

    public IVariant getHeadVariant() {
        if(cachedVariant == null) {
            cachedVariant = this.getHeadType().getVariant(this.getBlock());
        }
        return cachedVariant;
    }

    public float getOffset() {
        return this.getHeadType().getYOffset();
    }

    public Direction getDirection() {
        return this.getBlockState().get(BlockAnimalSkull.FACING_EXCEPT_DOWN);
    }

    public Direction getTopDirection() {
        return this.getBlockState().get(BlockAnimalSkull.TOP_FACING);
    }

    public float getTopRotation() {
        return this.getTopDirection().getHorizontalAngle();
    }

}
