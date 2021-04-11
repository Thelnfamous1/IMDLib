package dev.itsmeow.imdlib;

import dev.itsmeow.imdlib.client.IMDLibClient;
import dev.itsmeow.imdlib.client.render.RenderFactory;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IMDLib.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestModClient {

    public static final RenderFactory R = IMDLibClient.getRenderRegistry(IMDLib.ID);

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        QuadrupedModel model = new QuadrupedModel<TestMod.TestEntity>(6, 1F, false, 4.0F, 4.0F, 2.0F, 2.0F, 24);
        R.addRender(TestMod.TEST_ENTITY.getEntityType(), 1F, r -> r.tSingle("empty").mSingle(model));
    }
}
