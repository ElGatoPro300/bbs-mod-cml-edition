package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.bridge.EntityRenderStateBridge;
import mchorse.bbs_mod.client.renderer.MorphRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState>
{
    @Inject(method = "updateRenderState", at = @At("RETURN"))
    public void bbs$onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo ci)
    {
        ((EntityRenderStateBridge) state).bbs$setEntity(entity);
        ((EntityRenderStateBridge) state).bbs$setTickDelta(tickDelta);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void bbs$onRender(S state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci)
    {
        LivingEntity entity = ((EntityRenderStateBridge) state).bbs$getEntity();

        if (entity != null)
        {
            float tickDelta = ((EntityRenderStateBridge) state).bbs$getTickDelta();
            float whiteOverlayProgress = 0;

            if (this instanceof LivingEntityRendererInvoker invoker)
            {
                 whiteOverlayProgress = invoker.bbs$getAnimationCounter(state);
            }

            int o = LivingEntityRenderer.getOverlay(state, whiteOverlayProgress);

            if (MorphRenderer.renderLivingEntity(entity, 0F, tickDelta, matrixStack, vertexConsumerProvider, i, o))
            {
                ci.cancel();
            }
        }
    }
}