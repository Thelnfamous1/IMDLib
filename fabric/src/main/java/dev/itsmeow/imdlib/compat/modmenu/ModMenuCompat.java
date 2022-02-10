package dev.itsmeow.imdlib.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.itsmeow.imdlib.mixin.ClothConfigScreenAccessor;
import dev.itsmeow.imdlib.util.SafePlatform;
import dev.itsmeow.imdlib.util.config.CommonFabricConfigContainer;
import dev.itsmeow.imdlib.util.config.FabricConfigContainer;
import dev.itsmeow.imdlib.util.config.ServerFabricConfigContainer;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import me.shedaniel.fiber2cloth.api.Fiber2Cloth;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Hack the classloader
        Supplier<Supplier<ConfigScreenFactory<?>>> supplier = () -> () -> new ClothConfigScreenFactory(SafePlatform.modId());
        return SafePlatform.isModLoaded("cloth-config") ? supplier.get().get() : screen -> null;
    }

    public static class ClothConfigScreenFactory implements ConfigScreenFactory<Screen> {

        private final String modId;

        public ClothConfigScreenFactory(String modId) {
            this.modId = modId;
        }

        @Override
        public Screen create(Screen parent) {
            ConfigTreeBuilder b = ConfigTree.builder();
            for (FabricConfigContainer c : CommonFabricConfigContainer.INSTANCES) {
                c.getBranch().detach();
                b.withChild(c.getBranch());
            }
            Map<File, ServerFabricConfigContainer> serverConfigs = new HashMap<>();
            File defaultConfig = new File(FabricLoader.getInstance().getGameDir().resolve("defaultconfigs").toFile(), ServerFabricConfigContainer.INSTANCE.getConfigName() + ".json5");
            if(defaultConfig.exists()) {
                ServerFabricConfigContainer c = new ServerFabricConfigContainer();
                try {
                    c.loadFromFile(defaultConfig);
                } catch (ValueDeserializationException | IOException e) {}
                c.getBranch().detach();
                b.withChild(c.getBranch());
                serverConfigs.put(defaultConfig, c);
            }
            try {
                LevelStorageSource levelSource = Minecraft.getInstance().getLevelSource();
                for (LevelSummary level : levelSource.getLevelList()) {
                    File config = levelSource.getBaseDir().resolve(level.getLevelId()).resolve("serverconfig/" + ServerFabricConfigContainer.INSTANCE.getConfigName() + ".json5").toFile();
                    if (config.exists()) {
                        ServerFabricConfigContainer c = new ServerFabricConfigContainer(level.getLevelId());
                        try {
                            c.loadFromFile(config);
                        } catch (ValueDeserializationException | IOException e) {
                            continue;
                        }
                        c.getBranch().detach();
                        b.withChild(c.getBranch());
                        serverConfigs.put(config, c);
                    }
                }
            } catch (LevelStorageException e) {
            }
            Fiber2Cloth fiber2Cloth = Fiber2Cloth.create(parent, modId, b.build(), modId).setTitleText(new TranslatableComponent("config." + modId)).setSaveRunnable(() -> {
                for (FabricConfigContainer c : CommonFabricConfigContainer.INSTANCES) {
                    c.saveBranch(c.getConfigFile(null), b.lookupBranch(c.getConfigName()));
                }
                for (File file : serverConfigs.keySet()) {
                    ServerFabricConfigContainer c = serverConfigs.get(file);
                    c.saveBranch(file, b.lookupBranch(c.getConfigName()));
                }
            });
            Screen screen = fiber2Cloth.setAfterInitConsumer(initialized -> {
                if (initialized instanceof ClothConfigScreen) {
                    ClothConfigScreen s = (ClothConfigScreen) initialized;
                    List<ClothConfigTabButton> buttons = ((ClothConfigScreenAccessor) s).getTabButtons();
                    for (ClothConfigTabButton btn : buttons) {
                        if (btn.getMessage().getString().matches("config\\." + modId + "\\.[\\S\\s]+?-" + modId + "-server$")) {
                            String saveName = btn.getMessage().getString().substring(("config." + modId + ".").length(), btn.getMessage().getString().length() - ("-" + modId + "-server").length());
                            String newText = "World: " + saveName;
                            btn.setMessage(new TextComponent(newText));
                            btn.setWidth(Minecraft.getInstance().font.width(newText) + 8);
                        }
                    }
                }
            }).build().getScreen();
            return screen;
        }
    }
}
