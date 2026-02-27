package mchorse.bbs_mod.client;

import net.minecraft.client.MinecraftClient;

public class Probe {
    public void test() {
        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        var beDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
    }
}
