package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.forms.renderers.utils.RenderTask;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
    @Shadow
    public abstract <E extends BlockEntity> void render(E blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider);

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private static void onRenderMain(CallbackInfo info)
    {
        if (BBSSettings.chromaSkyEnabled.get() && !BBSSettings.chromaSkyTerrain.get())
        {
            info.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderToo(BlockEntity blockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, CallbackInfo info)
    {
        if (BBSSettings.chromaSkyEnabled.get() && !BBSSettings.chromaSkyTerrain.get())
        {
            info.cancel();
            return;
        }

        if (BBSRendering.tasks != null && blockEntity instanceof ModelBlockEntity modelBlock && modelBlock.getProperties().getForm() != null)
        {
            Matrix4f position = new Matrix4f(matrixStack.peek().getPositionMatrix());
            Matrix3f normal = new Matrix3f(matrixStack.peek().getNormalMatrix());
            double distance = blockEntity.getPos().getSquaredDistance(MinecraftClient.getInstance().gameRenderer.getCamera().getPos());
            int layer = modelBlock.getProperties().getForm().layer.get();

            BBSRendering.tasks.add(new RenderTask(() ->
            {
                MatrixStack stack = new MatrixStack();

                stack.peek().getPositionMatrix().set(position);
                stack.peek().getNormalMatrix().set(normal);

                List<RenderTask> tasks = BBSRendering.tasks;
                BBSRendering.tasks = null;

                try
                {
                    this.render(blockEntity, tickDelta, stack, vertexConsumerProvider);
                }
                finally
                {
                    BBSRendering.tasks = tasks;
                }
            }, distance, layer));

            info.cancel();
        }
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void onRenderEntity(CallbackInfoReturnable<Boolean> info)
    {
        if (BBSSettings.chromaSkyEnabled.get() && !BBSSettings.chromaSkyTerrain.get())
        {
            info.setReturnValue(false);
        }
    }
}
