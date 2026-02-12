package mchorse.bbs_mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.client.renderer.MorphRenderer;
import mchorse.bbs_mod.forms.renderers.utils.RenderTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
    @Shadow
    public abstract <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        if (BBSRendering.tasks != null)
        {
            Matrix4f position = new Matrix4f(matrices.peek().getPositionMatrix());
            Matrix3f normal = new Matrix3f(matrices.peek().getNormalMatrix());
            double distance = x * x + y * y + z * z;
            int layer = 0;

            BBSRendering.tasks.add(new RenderTask(() ->
            {
                MatrixStack stack = new MatrixStack();
                stack.peek().getPositionMatrix().set(position);
                stack.peek().getNormalMatrix().set(normal);

                List<RenderTask> tasks = BBSRendering.tasks;
                BBSRendering.tasks = null;

                try
                {
                    this.render(entity, x, y, z, yaw, tickDelta, stack, vertexConsumers, light);
                }
                finally
                {
                    BBSRendering.tasks = tasks;
                }
            }, distance, layer));

            ci.cancel();
        }
    }

    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
        )
    )
    private <E extends Entity> void wrapRender(
        EntityRenderer<E> renderer, E entity, float yaw, float tickDelta,
        MatrixStack matrices, VertexConsumerProvider vcp, int light,
        Operation<Void> original
    ) {
        if (entity instanceof LivingEntity livingEntity)
        {
            float whiteOverlayProgress = 0;

            if (renderer instanceof LivingEntityRendererInvoker invoker)
            {
                whiteOverlayProgress = invoker.bbs$getAnimationCounter(livingEntity, tickDelta);
            }

            int o = LivingEntityRenderer.getOverlay(livingEntity, whiteOverlayProgress);

            if (MorphRenderer.renderLivingEntity(livingEntity, yaw, tickDelta, matrices, vcp, light, o))
            {
                return;
            }
        }

        original.call(renderer, entity, yaw, tickDelta, matrices, vcp, light);
    }
}