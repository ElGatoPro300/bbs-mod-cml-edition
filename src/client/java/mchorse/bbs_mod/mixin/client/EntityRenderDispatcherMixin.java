package mchorse.bbs_mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mchorse.bbs_mod.client.renderer.MorphRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin
{
/*
    @WrapOperation(
        method = "render(Lnet/minecraft/entity/Entity;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
        )
    )
    private <E extends Entity, S extends EntityRenderState> void wrapRender(
        EntityRenderer<E, S> renderer, S state,
        MatrixStack matrices, VertexConsumerProvider vcp, int light,
        Operation<Void> original,
        @Local(argsOnly = true) E entity,
        @Local(argsOnly = true) float tickDelta
    ) {
        if (entity instanceof LivingEntity livingEntity && state instanceof LivingEntityRenderState livingState)
        {
            float whiteOverlayProgress = 0;

            if (renderer instanceof LivingEntityRendererInvoker invoker)
            {
                whiteOverlayProgress = invoker.bbs$getAnimationCounter(livingState);
            }

            int o = LivingEntityRenderer.getOverlay(livingState, whiteOverlayProgress);

            if (MorphRenderer.renderLivingEntity(livingEntity, matrices, vcp, light, o, tickDelta))
            {
                return;
            }
        }

        original.call(renderer, state, matrices, vcp, light);
    }
*/
}