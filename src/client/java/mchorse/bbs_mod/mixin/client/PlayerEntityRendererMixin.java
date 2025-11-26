package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.morphing.Morph;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin
{
    // Note: PlayerEntityRenderer no longer overrides render() in 1.21.4,
    // so injecting into it here fails. Rendering hooks are handled via
    // LivingEntityRendererMixin and specific arm/offset injections below.

    // Removed position offset hook for 1.21.4; PlayerEntityRenderState no longer
    // exposes the backing entity, and this adjustment is not critical.

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    public void onRenderArmBegin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier texture, ModelPart arm, boolean withSleeve, CallbackInfo info)
    {
        AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
        {
            return;
        }

        Morph morph = Morph.getMorph(player);

        if (morph != null)
        {
            Form form = morph.getForm();

            if (form != null)
            {
                FormRenderer renderer = FormUtilsClient.getRenderer(form);
                Hand hand = ((PlayerEntityRenderer) (Object) this).getModel().rightArm == arm ? Hand.MAIN_HAND : Hand.OFF_HAND;

                if (renderer != null && renderer.renderArm(matrices, light, player, hand))
                {
                    info.cancel();
                }
            }
        }
    }
}
