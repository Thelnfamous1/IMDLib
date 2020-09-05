package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class ModSpawnEggItem extends SpawnEggItem {

    private final EntityType<?> type;
    private final String modid;
    private final String name;

    public ModSpawnEggItem(EntityTypeContainer<?> container) {
        super(container.entityType, container.eggColorSolid, container.eggColorSpot, new Properties().group(ItemGroup.MISC));
        this.type = container.entityType;
        this.modid = container.getModId();
        this.name = container.entityName.toLowerCase();
        this.setRegistryName(container.getModId(), name + "_spawn_egg");
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

    public DataProvider getDataProvider(GatherDataEvent event) {
        return new DataProvider(this, event.getGenerator(), event.getExistingFileHelper());
    }

    public static class DataProvider extends ItemModelProvider {

        private String name;

        public DataProvider(ModSpawnEggItem i, DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, i.modid, existingFileHelper);
            this.name = i.name;
        }

        @Override
        public String getName() {
            return name + "_spawn_egg";
        }

        @Override
        protected void registerModels() {
            this.withExistingParent(name + "_spawn_egg", new ResourceLocation("minecraft", "item/template_spawn_egg"));
        }

    }
}
