package mchorse.bbs_mod.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.entity.GunProjectileEntity;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.renderers.FormRenderType;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import mchorse.bbs_mod.items.GunProperties;
import mchorse.bbs_mod.utils.MatrixStackUtils;
import mchorse.bbs_mod.utils.interps.Lerps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class GunProjectileEntityRenderer extends EntityRenderer<GunProjectileEntity, GunProjectileEntityRenderState>
{
    public GunProjectileEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public GunProjectileEntityRenderState createRenderState()
    {
        return new GunProjectileEntityRenderState();
    }

    @Override
    public void updateRenderState(GunProjectileEntity entity, GunProjectileEntityRenderState state, float tickDelta)
    {
        super.updateRenderState(entity, state, tickDelta);
        state.projectile = entity;
        state.properties = entity.getProperties();
        state.yaw = entity.getYaw();
        state.pitch = entity.getPitch();
        state.prevYaw = entity.prevYaw;
        state.prevPitch = entity.prevPitch;
        state.age = entity.age;
        state.tickDelta = tickDelta;
    }

    public Identifier getTexture(GunProjectileEntityRenderState state)
    {
        return Identifier.of("minecraft", "textures/entity/player/wide/steve.png");
    }

    @Override
    public void render(GunProjectileEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();

        GunProperties properties = state.properties;
        int out = properties.lifeSpan - 2;
        float tickDelta = state.tickDelta;

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, state.prevYaw, state.yaw);
        float pitch = MathHelper.lerpAngleDegrees(tickDelta, state.prevPitch, state.pitch);
        float scale = Lerps.envelope(state.age + tickDelta, 0, properties.fadeIn, out - properties.fadeOut, out);

        if (properties.yaw) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));
        if (properties.pitch) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch));
        matrices.scale(scale, scale, scale);
        MatrixStackUtils.applyTransform(matrices, properties.projectileTransform);

        RenderSystem.enableDepthTest();
        FormUtilsClient.render(state.projectile.getForm(), new FormRenderingContext()
            .set(FormRenderType.ENTITY, state.projectile.getEntity(), matrices, light, OverlayTexture.DEFAULT_UV, tickDelta)
            .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
        RenderSystem.disableDepthTest();

        matrices.pop();

        super.render(state, matrices, vertexConsumers, light);
    }
}