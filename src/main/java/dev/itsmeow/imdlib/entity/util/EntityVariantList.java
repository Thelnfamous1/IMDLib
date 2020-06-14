package dev.itsmeow.imdlib.entity.util;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

public class EntityVariantList {

    private final ArrayList<IVariant> variantList;
    private final HashMap<String, IVariant> nameMap;
    public static final IVariant EMPTY_VARIANT = new EntityVariant("minecraft", "empty", false);

    public EntityVariantList(int size) {
        this.variantList = new ArrayList<IVariant>(size);
        this.nameMap = new HashMap<String, IVariant>(size);
    }

    @Deprecated
    public IVariant getVariantForIndex(int index) {
        return variantList.get(index);
    }

    @Deprecated
    public int getVariantIndex(IVariant variant) {
        return variantList.indexOf(variant);
    }

    @Nullable
    @CheckForNull
    public IVariant getVariantForName(String name) {
        if(name == null || "".equals(name) || !nameMap.containsKey(name)) {
            return EMPTY_VARIANT; // stop crashing if name is invalid
        }
        return nameMap.get(name);
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
