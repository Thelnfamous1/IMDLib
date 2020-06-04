package dev.itsmeow.imdlib.entity.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;

public class EntityVariantList {

    private final ArrayList<IVariant> variantList;
    private final HashMap<String, IVariant> nameMap;
    private static final IVariant EMPTY_VARIANT = new EntityVariant("minecraft", "empty", false);

    public EntityVariantList(int size) {
        this.variantList = new ArrayList<IVariant>(size);
        this.nameMap = new HashMap<String, IVariant>(size);
    }

    public IVariant getVariant(int index) {
        return variantList.get(index);
    }

    public int getVariantIndex(String variant) {
        return variantList.indexOf(getVariant(variant));
    }

    public int getVariantIndex(IVariant variant) {
        return variantList.indexOf(variant);
    }

    public IVariant getVariant(String name) {
        if(!nameMap.containsKey(name)) {
            return EMPTY_VARIANT; // stop crashing if name is invalid
        }
        return nameMap.get(name);
    }

    public ResourceLocation getTexture(String name) {
        return getVariant(name).getTexture();
    }

    public String getName(String name) {
        return getVariant(name).getName();
    }

    public ImmutableList<IVariant> getVariantList() {
        return ImmutableList.copyOf(this.variantList);
    }

    public void add(IVariant... variants) {
        for(IVariant variant : variants) {
            variantList.add(variant);
            nameMap.put(variant.getName(), variant);
        }
    }

}
