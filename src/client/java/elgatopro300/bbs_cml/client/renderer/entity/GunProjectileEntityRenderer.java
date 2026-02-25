package elgatopro300.bbs_cml.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.entity.GunProjectileEntity;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.renderers.FormRenderType;
import elgatopro300.bbs_cml.forms.renderers.FormRenderingContext;
import elgatopro300.bbs_cml.items.GunProperties;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.interps.Lerps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class GunProjectileEntityRenderer extends EntityRenderer<GunProjectileEntity>
{
    public GunProjectileEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public Identifier getTexture(GunProjectileEntity entity)
    {
        return new Identifier("minecraft:textures/entity/player/wide/steve.png");
    }

    @Override
    public void render(GunProjectileEntity projectile, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();

        GunProperties properties = projectile.getProperties();
        int out = properties.lifeSpan - 2;

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, projectile.prevYaw, projectile.getYaw());
        float pitch = MathHelper.lerpAngleDegrees(tickDelta, projectile.prevPitch, projectile.getPitch());
        float scale = Lerps.envelope(projectile.age + tickDelta, 0, properties.fadeIn, out - properties.fadeOut, out);

        if (properties.yaw) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));
        if (properties.pitch) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch));
        matrices.scale(scale, scale, scale);
        MatrixStackUtils.applyTransform(matrices, properties.projectileTransform);

        RenderSystem.enableDepthTest();
        FormUtilsClient.render(projectile.getForm(), new FormRenderingContext()
            .set(FormRenderType.ENTITY, projectile.getEntity(), matrices, light, OverlayTexture.DEFAULT_UV, tickDelta)
            .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
        RenderSystem.disableDepthTest();

        matrices.pop();

        super.render(projectile, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}