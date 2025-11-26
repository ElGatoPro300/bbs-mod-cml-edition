package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.forms.renderers.MobFormRenderer;
import mchorse.bbs_mod.utils.pose.Pose;
import mchorse.bbs_mod.utils.pose.PoseTransform;
import mchorse.bbs_mod.utils.pose.Transform;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin
{
    // Removed failing setAngles hook for 1.21.4; the invocation target
    // is no longer stable across models, causing injection scan to fail.

    @Inject(method = "render", at = @At("TAIL"))
    public void onRenderEnd(LivingEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info)
    {
        // Clear any stale cache data to keep model state consistent.
        MobFormRenderer.getCache().clear();
    }
}
