package dev.itsmeow.imdlib;

import dev.itsmeow.imdlib.entity.EntityRegistrarHandler;
import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.interfaces.IContainerEntity;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@Mod(IMDLib.ID)
public class TestMod {

    public static EntityRegistrarHandler H = new EntityRegistrarHandler(IMDLib.ID);

    private static EntityRegistrarHandler.ServerEntityConfiguration SERVER_CONFIG = null;
    public static ForgeConfigSpec SERVER_CONFIG_SPEC = null;

    private static EntityRegistrarHandler.ClientEntityConfiguration CLIENT_CONFIG = null;
    public static ForgeConfigSpec CLIENT_CONFIG_SPEC = null;

    public static EntityTypeContainer<TestEntity> TEST_ENTITY = H.add(
            EntityTypeContainer.Builder.create(TestEntity.class, TestEntity::new, "test_entity", MobEntity::func_233666_p_, IMDLib.ID)
            .size(1F, 1F)
            .egg(0x0, 0xf)
            .spawn(EntityClassification.CREATURE, 1000, 1, 1)
            .biomes(BiomeDictionary.Type.OVERWORLD)
            .config((holder, builder) -> {
                holder.put(builder.define("testKey", "test"));
            }, holder -> {
                System.out.println("LOAD: " + holder.getString("testKey"));
            })
    );

    public TestMod() {
        final Pair<EntityRegistrarHandler.ServerEntityConfiguration, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(H::serverConfig);
        SERVER_CONFIG_SPEC = specPair.getRight();
        SERVER_CONFIG = specPair.getLeft();
        final Pair<EntityRegistrarHandler.ClientEntityConfiguration, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(H::clientConfig);
        CLIENT_CONFIG_SPEC = specPair2.getRight();
        CLIENT_CONFIG = specPair2.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        H.subscribe(modBus);
        modBus.addListener(this::loadComplete);
        modBus.addListener(this::onLoad);
        modBus.addListener(this::onReload);
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG_SPEC);
    }

    private void onLoad(final ModConfig.Loading configEvent) {
        if(configEvent.getConfig().getSpec() == SERVER_CONFIG_SPEC) {
            SERVER_CONFIG.onLoad();
        } else if(configEvent.getConfig().getSpec() == CLIENT_CONFIG_SPEC) {
            CLIENT_CONFIG.onLoad();
        }
    }

    private void onReload(final ModConfig.Reloading configEvent) {
        if(configEvent.getConfig().getSpec() == SERVER_CONFIG_SPEC) {
            SERVER_CONFIG.onLoad();
        } else if(configEvent.getConfig().getSpec() == CLIENT_CONFIG_SPEC) {
            CLIENT_CONFIG.onLoad();
        }
    }

    public static class TestEntity extends AnimalEntity implements IContainerEntity<TestEntity> {
        public TestEntity(EntityType<? extends TestEntity> type, World world) {
            super(type, world);
            this.setCustomName(new StringTextComponent(TEST_ENTITY.getCustomConfiguration().getString("testKey")));
        }

        @Nullable
        @Override
        public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) {
            return null;
        }

        @Override
        public TestEntity getImplementation() {
            return this;
        }

        @Override
        public EntityTypeContainer<? extends TestEntity> getContainer() {
            return TEST_ENTITY;
        }

        @Override
        public boolean canDespawn(double distanceToClosestPlayer) {
            return despawn(distanceToClosestPlayer);
        }
    }

}
