package dev.itsmeow.imdlib.item;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
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

    public static class DataProvider extends ItemModelProvider {

        private EntityRegistrarHandler handler;

        public DataProvider(EntityRegistrarHandler r, DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, r.modid, existingFileHelper);
            this.handler = r;
        }

        @Override
        public String getName() {
            return this.modid + "_spawn_eggs";
        }

        @Override
        protected void registerModels() {
            for(String name : handler.ENTITIES.keySet()) {
                this.withExistingParent(name + "_spawn_egg", new ResourceLocation("minecraft", "item/template_spawn_egg"));
            }
        }

    }
}
