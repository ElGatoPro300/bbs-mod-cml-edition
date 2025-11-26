package mchorse.bbs_mod.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;

public class EntityRendererDispatcherInvoker
{
    public static void bbs$renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius)
    {
        // Shadow rendering helper removed for 1.21.4; no-op.
    }
}
