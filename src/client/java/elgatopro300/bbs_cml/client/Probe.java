package elgatopro300.bbs_cml.client;

import net.minecraft.client.MinecraftClient;

public class Probe {
    public void test() {
        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        var beDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
    }
}
