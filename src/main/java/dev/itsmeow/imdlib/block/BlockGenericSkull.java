package dev.itsmeow.imdlib.block;

import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockGenericSkull extends BlockAnimalSkull {

    public final HeadType type;

    public BlockGenericSkull(HeadType type, String id) {
        super();
        this.setRegistryName(type.getMod(), type.getName() + "_" + id);
        this.type = type;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader reader) {
        return type.createTE();
    }

}
