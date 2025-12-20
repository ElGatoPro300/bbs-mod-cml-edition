package mchorse.bbs_mod.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.cubic.render.vanilla.ArmorRenderer;
import mchorse.bbs_mod.entity.ActorEntity;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.renderers.FormRenderType;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ActorEntityRenderer extends EntityRenderer<ActorEntity, ActorEntityRenderer.ActorEntityState>
{
    public static ArmorRenderer armorRenderer;

    public ActorEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);

        armorRenderer = new ArmorRenderer(
            new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
            new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
            ctx.getModelManager()
        );
    }

    @Override
    public ActorEntityState createRenderState()
    {
        return new ActorEntityState();
    }

    public void updateRenderState(ActorEntity entity, ActorEntityState state, float tickDelta)
    {
        super.updateRenderState(entity, state, tickDelta);
        state.actorEntity = entity;
        state.bodyYaw = entity.bodyYaw;
        state.prevBodyYaw = entity.prevBodyYaw;
        state.deathTime = entity.deathTime;
        state.isInSleepingPose = entity.isInPose(EntityPose.SLEEPING);
        state.tickDelta = tickDelta;
    }

    public Identifier getTexture(ActorEntityState state)
    {
        return Identifier.of("minecraft:textures/entity/player/wide/steve.png");
    }

    @Override
    public void render(ActorEntityState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();

        float bodyYaw = MathHelper.lerpAngleDegrees(state.tickDelta, state.prevBodyYaw, state.bodyYaw);
        int overlay = OverlayTexture.getU(0F) | (OverlayTexture.getV(state.actorEntity.hurtTime > 0 || state.actorEntity.deathTime > 0) << 16);

        this.setupTransforms(state, matrices, bodyYaw);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        FormUtilsClient.render(state.actorEntity.getForm(), new FormRenderingContext()
            .set(FormRenderType.ENTITY, state.actorEntity.getEntity(), matrices, light, overlay, state.tickDelta)
            .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();

        super.render(state, matrices, vertexConsumers, light);
    }

    protected void setupTransforms(ActorEntityState state, MatrixStack matrices, float bodyYaw)
    {
        if (!state.isInSleepingPose)
        {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bodyYaw));
        }

        if (state.deathTime > 0)
        {
            float deathAngle = (state.deathTime + state.tickDelta - 1F) / 20F * 1.6F;

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Math.min(MathHelper.sqrt(deathAngle), 1F) * 90F));
        }
    }

    public static class ActorEntityState extends EntityRenderState
    {
        public ActorEntity actorEntity;
        public float bodyYaw;
        public float prevBodyYaw;
        public int deathTime;
        public boolean isInSleepingPose;
        public float tickDelta;
    }
}