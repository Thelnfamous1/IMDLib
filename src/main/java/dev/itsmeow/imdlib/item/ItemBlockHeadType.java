package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.util.IVariant;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;

public class ItemBlockHeadType extends ItemBlockSkull {

    private final HeadType type;

    public ItemBlockHeadType(Block block, HeadType type, String id, IVariant variant, ItemGroup group) {
        super(block, type.getPlacementType(), id, variant, new Properties().group(group));
        this.type = type;
    }

    public ItemBlockHeadType(Block block, HeadType type, String id, IVariant variant, Properties prop) {
        super(block, type.getPlacementType(), id, variant, prop);
        this.type = type;
    }

    @Override
    public String getTranslationKey() {
        return "block" + "." + type.getMod() + "." + this.type.getName();
    }

}
