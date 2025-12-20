package mchorse.bbs_mod.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.entity.GunProjectileEntity;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.renderers.FormRenderType;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import mchorse.bbs_mod.items.GunProperties;
import mchorse.bbs_mod.utils.MatrixStackUtils;
import mchorse.bbs_mod.utils.interps.Lerps;
import mchorse.bbs_mod.utils.pose.Transform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class GunProjectileEntityRenderer extends EntityRenderer<GunProjectileEntity, GunProjectileEntityRenderer.GunProjectileEntityState>
{
    public GunProjectileEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public GunProjectileEntityState createRenderState()
    {
        return new GunProjectileEntityState();
    }

    public void updateRenderState(GunProjectileEntity entity, GunProjectileEntityState state, float tickDelta)
    {
        super.updateRenderState(entity, state, tickDelta);
        state.projectile = entity;
        state.tickDelta = tickDelta;
        
        GunProperties properties = entity.getProperties();
        state.properties = properties; // Careful, GunProperties might be mutable or large, but it's likely fine
        state.lifeSpan = properties.lifeSpan;
        state.fadeIn = properties.fadeIn;
        state.fadeOut = properties.fadeOut;
        state.yaw = properties.yaw;
        state.pitch = properties.pitch;
        state.projectileTransform = properties.projectileTransform;
        
        state.age = entity.age;
        state.prevYaw = entity.prevYaw;
        state.entityYaw = entity.getYaw();
        state.prevPitch = entity.prevPitch;
        state.entityPitch = entity.getPitch();
        state.form = entity.getForm();
    }

    public Identifier getTexture(GunProjectileEntityState state)
    {
        return Identifier.of("minecraft", "textures/entity/player/wide/steve.png");
    }

    @Override
    public void render(GunProjectileEntityState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();

        GunProperties properties = state.properties; // Use properties from state
        // Or better, use individual fields if we copied them
        
        int out = state.lifeSpan - 2;

        float bodyYaw = MathHelper.lerpAngleDegrees(state.tickDelta, state.prevYaw, state.entityYaw);
        float pitch = MathHelper.lerpAngleDegrees(state.tickDelta, state.prevPitch, state.entityPitch);
        float scale = Lerps.envelope(state.age + state.tickDelta, 0, state.fadeIn, out - state.fadeOut, out);

        if (state.yaw) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));
        if (state.pitch) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch));
        matrices.scale(scale, scale, scale);
        MatrixStackUtils.applyTransform(matrices, state.projectileTransform);

        RenderSystem.enableDepthTest();
        FormUtilsClient.render(state.form, new FormRenderingContext()
            .set(FormRenderType.ENTITY, state.projectile.getEntity(), matrices, light, OverlayTexture.DEFAULT_UV, state.tickDelta)
            .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
        RenderSystem.disableDepthTest();

        matrices.pop();

        super.render(state, matrices, vertexConsumers, light);
    }

    public static class GunProjectileEntityState extends EntityRenderState
    {
        public GunProjectileEntity projectile;
        public float tickDelta;
        public GunProperties properties;
        public int lifeSpan;
        public int fadeIn;
        public int fadeOut;
        public boolean yaw;
        public boolean pitch;
        public Transform projectileTransform;
        
        public int age;
        public float prevYaw;
        public float entityYaw;
        public float prevPitch;
        public float entityPitch;
        public mchorse.bbs_mod.forms.forms.Form form;
    }
}