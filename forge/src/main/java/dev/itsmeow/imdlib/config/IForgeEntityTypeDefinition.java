package dev.itsmeow.imdlib.config;

import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

public interface IForgeEntityTypeDefinition<T extends Mob> extends IEntityTypeDefinition<T> {
    ForgeEntityTypeContainerConfigHandler.CustomConfigurationLoad getCustomConfigLoad();

    @Nullable
    ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit getCustomConfigInit();

    @Nullable
    ForgeEntityTypeContainerConfigHandler.CustomConfigurationLoad getCustomClientConfigLoad();

    @Nullable
    ForgeEntityTypeContainerConfigHandler.CustomConfigurationInit getCustomClientConfigInit();
}
