package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;


public class ItemBlockHeadType extends ItemBlockSkull {

    private final HeadType type;

    public ItemBlockHeadType(Block block, HeadType type, String id, IVariant variant, CreativeModeTab group) {
        super(block, type.getPlacementType(), id, variant, new Item.Properties().tab(group));
        this.type = type;
    }

    public ItemBlockHeadType(Block block, HeadType type, String id, IVariant variant, Item.Properties prop) {
        super(block, type.getPlacementType(), id, variant, prop);
        this.type = type;
    }

    @Override
    public String getDescriptionId() {
        return "block" + "." + type.getMod() + "." + this.type.getName();
    }

}
