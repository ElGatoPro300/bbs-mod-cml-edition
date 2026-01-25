package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.client.renderer.MorphRenderer;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.FormRenderer;
import mchorse.bbs_mod.morphing.Morph;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mchorse.bbs_mod.ducks.IEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.MinecraftClient;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin
{
    /*@Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(PlayerEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info)
    {
        AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity) ((IEntityRenderState) state).bbs$getEntity();
        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

        if (abstractClientPlayerEntity != null && MorphRenderer.renderPlayer(abstractClientPlayerEntity, state.bodyYaw, tickDelta, matrixStack, vertexConsumerProvider, i))
        {
            info.cancel();
        }
    }*/

    @Inject(method = "getPositionOffset", at = @At("HEAD"), cancellable = true)
    public void onPositionOffset(PlayerEntityRenderState state, CallbackInfoReturnable<Vec3d> info)
    {
        AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity) ((IEntityRenderState) state).bbs$getEntity();
        Morph morph = Morph.getMorph(abstractClientPlayerEntity);

        if (morph != null && morph.getForm() != null)
        {
            info.setReturnValue(Vec3d.ZERO);
        }
    }

    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    public void onRenderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, net.minecraft.util.Identifier skin, boolean mainArm, CallbackInfo info)
    {
        AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;
        Morph morph = Morph.getMorph(player);

        if (morph != null)
        {
            Form form = morph.getForm();

            if (form != null)
            {
                FormRenderer renderer = FormUtilsClient.getRenderer(form);
                Hand hand = player.getMainArm() == net.minecraft.util.Arm.RIGHT ? Hand.MAIN_HAND : Hand.OFF_HAND;

                if (renderer != null && renderer.renderArm(matrices, light, player, hand))
                {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    public void onRenderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, net.minecraft.util.Identifier skin, boolean mainArm, CallbackInfo info)
    {
        AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;
        Morph morph = Morph.getMorph(player);

        if (morph != null)
        {
            Form form = morph.getForm();

            if (form != null)
            {
                FormRenderer renderer = FormUtilsClient.getRenderer(form);
                Hand hand = player.getMainArm() == net.minecraft.util.Arm.LEFT ? Hand.MAIN_HAND : Hand.OFF_HAND;

                if (renderer != null && renderer.renderArm(matrices, light, player, hand))
                {
                    info.cancel();
                }
            }
        }
    }
}