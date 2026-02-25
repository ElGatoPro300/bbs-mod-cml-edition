package elgatopro300.bbs_cml.client.renderer;

import elgatopro300.bbs_cml.blocks.entities.TriggerBlockEntity;
import elgatopro300.bbs_cml.graphics.Draw;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashSet;
import java.util.Set;

public class TriggerBlockEntityRenderer implements BlockEntityRenderer<TriggerBlockEntity, BlockEntityRenderState>
{
    public static final Set<TriggerBlockEntity> capturedTriggerBlocks = new HashSet<>();

    public TriggerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
    {}

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void render(BlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState)
    {
        
    }
}
