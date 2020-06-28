package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ModSpawnEggItem extends SpawnEggItem {

    private final EntityType<?> type;
    private final String modid;

    public ModSpawnEggItem(EntityTypeContainer<?> container) {
        super(container.entityType, container.eggColorSolid, container.eggColorSpot, new Properties().group(ItemGroup.MISC));
        this.type = container.entityType;
        this.modid = container.getModId();
        this.setRegistryName(container.getModId(), container.entityName.toLowerCase().toString() + "_spawn_egg");
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if(type != null) {
            return "entity." + modid + "." + this.type.getRegistryName().getPath();
        }
        return "item." + modid + ".emptyegg";
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("misc." + modid + ".eggorder",
        new TranslationTextComponent(this.getTranslationKey(stack)));
    }

    @Override
    public EntityType<?> getType(CompoundNBT tag) {
        return this.type;
    }

    @Override
    public boolean hasType(CompoundNBT tag, EntityType<?> type) {
        return type == this.type;
    }

}
