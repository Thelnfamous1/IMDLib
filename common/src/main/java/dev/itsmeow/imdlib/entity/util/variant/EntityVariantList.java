package dev.itsmeow.imdlib.entity.util.variant;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class EntityVariantList {

    private final ArrayList<IVariant> variantList;
    private final HashMap<String, IVariant> nameMap;

    public EntityVariantList(int size) {
        this.variantList = new ArrayList<>(size);
        this.nameMap = new HashMap<>(size);
    }

    @Deprecated
    public Optional<IVariant> getVariantForIndex(int index) {
        if(index >= variantList.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(variantList.get(index));
    }

    @Deprecated
    public int getVariantIndex(IVariant variant) {
        return variantList.indexOf(variant);
    }

    public Optional<IVariant> getVariantForName(String name) {
        return Optional.ofNullable(nameMap.get(name));
    }

    public ImmutableList<IVariant> getVariantList() {
        return ImmutableList.copyOf(this.variantList);
    }

    public void add(IVariant... variants) {
        for (IVariant variant : variants) {
            variantList.add(variant);
            nameMap.put(variant.getName(), variant);
        }
    }

}
