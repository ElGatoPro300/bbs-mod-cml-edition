package elgatopro300.bbs_cml.client.renderer;

import elgatopro300.bbs_cml.blocks.entities.TriggerBlockEntity;
import elgatopro300.bbs_cml.graphics.Draw;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashSet;
import java.util.Set;

public class TriggerBlockEntityRenderer implements BlockEntityRenderer<TriggerBlockEntity>
{
    public static final Set<TriggerBlockEntity> capturedTriggerBlocks = new HashSet<>();

    public TriggerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
    {}

    @Override
    public void render(TriggerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        capturedTriggerBlocks.add(entity);

        MinecraftClient mc = MinecraftClient.getInstance();
        
        if (mc.options.debugEnabled)
        {
            matrices.push();
            matrices.translate(0.5D, 0, 0.5D);
            /* Render green debug box for triggers */
            Draw.renderBox(matrices, -0.5D, 0, -0.5D, 1, 1, 1, 0, 1F, 0.5F, 0.5F);
            matrices.pop();
        }
    }
}
