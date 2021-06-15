package dev.itsmeow.imdlib.blockentity;

import dev.itsmeow.imdlib.block.AnimalSkullBlock;
import dev.itsmeow.imdlib.client.render.RenderGenericHead;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.HeadType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HeadBlockEntity extends BlockEntity {

    public static final BlockEntityType<HeadBlockEntity> HEAD_TYPE = BlockEntityType.Builder.of(HeadBlockEntity::new, HeadType.getAllBlocks()).build(null);
    private HeadType cachedType = null;
    private IVariant cachedVariant = null;


    public HeadBlockEntity() {
        super(HEAD_TYPE);
    }
    public HeadBlockEntity(HeadType type) {
        super(HEAD_TYPE);
    }

    public static void registerType(RegistryEvent.Register<BlockEntityType<?>> event, String modid) {
        HEAD_TYPE.setRegistryName(modid, "head");
        event.getRegistry().register(HEAD_TYPE);
    }

    @Environment(EnvType.CLIENT)
    public static void registerTypeRender() {
        ClientRegistry.bindTileEntityRenderer(HEAD_TYPE, RenderGenericHead::new);
    }

    @Environment(EnvType.CLIENT)
    public EntityModel<? extends Entity> getNewModel() {
        return this.getHeadType().getModelSupplier().get().get();
    }

    private Block getBlock() {
        return this.getBlockState().getBlock();
    }

    public HeadType getHeadType() {
        if (cachedType == null) {
            cachedType = HeadType.valueOf(this.getBlock());
        }
        return cachedType;
    }

    public ResourceLocation getTexture() {
        return this.getHeadVariant().getTexture(null);
    }

    public IVariant getHeadVariant() {
        if (cachedVariant == null) {
            cachedVariant = this.getHeadType().getVariant(this.getBlock());
        }
        return cachedVariant;
    }

    public float getOffset() {
        return this.getHeadType().getYOffset();
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(AnimalSkullBlock.FACING_EXCEPT_DOWN);
    }

    public Direction getTopDirection() {
        return this.getBlockState().getValue(AnimalSkullBlock.TOP_FACING);
    }

    public float getTopRotation() {
        return this.getTopDirection().toYRot();
    }

}
