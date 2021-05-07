package dev.itsmeow.imdlib.block;

import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GenericSkullBlock extends AnimalSkullBlock implements EntityBlock {

    public final HeadType type;

    public GenericSkullBlock(HeadType type, String id) {
        super();

        //TODO this.setRegistryName(type.getMod(), type.getName() + "_" + id);
        this.type = type;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return type.createTE();
    }

}
