package mchorse.bbs_mod.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.cubic.render.vanilla.ArmorRenderer;
import mchorse.bbs_mod.entity.ActorEntity;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.renderers.FormRenderType;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ActorEntityRenderer extends EntityRenderer<ActorEntity, EntityRenderState>
{
    public static ArmorRenderer armorRenderer;
    private ActorEntity currentEntity;
    private float currentTickDelta;

    public ActorEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);

        armorRenderer = new ArmorRenderer(
            new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
            new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
            ctx.getModelManager()
        );

        // Shadow radius assignment not available in 1.21.4 mappings; skip.
    }

    public Identifier getTexture(ActorEntity entity)
    {
        return Identifier.of("minecraft:textures/entity/player/wide/steve.png");
    }

    public void render(ActorEntity livingEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
        int overlay = net.minecraft.client.render.OverlayTexture.DEFAULT_UV;

        this.setupTransforms(livingEntity, matrices, bodyYaw, tickDelta);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        FormUtilsClient.render(livingEntity.getForm(), new FormRenderingContext()
            .set(FormRenderType.ENTITY, livingEntity.getEntity(), matrices, light, overlay, tickDelta)
            .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();

        // Skip base rendering; custom form rendering handles visuals.
    }

    public EntityRenderState createRenderState()
    {
        return new EntityRenderState();
    }

    public void updateRenderState(ActorEntity entity, float tickDelta, EntityRenderState state)
    {
        this.currentEntity = entity;
        this.currentTickDelta = tickDelta;
    }

    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        ActorEntity livingEntity = this.currentEntity;
        float tickDelta = this.currentTickDelta;

        if (livingEntity == null)
        {
            return;
        }

        matrices.push();

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
        int overlay = net.minecraft.client.render.OverlayTexture.DEFAULT_UV;

        this.setupTransforms(livingEntity, matrices, bodyYaw, tickDelta);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        FormUtilsClient.render(livingEntity.getForm(), new FormRenderingContext()
            .set(FormRenderType.ENTITY, livingEntity.getEntity(), matrices, light, overlay, tickDelta)
            .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    protected boolean isVisible(ActorEntity entity)
    {
        return !entity.isInvisible();
    }

    protected void setupTransforms(ActorEntity entity, MatrixStack matrices, float bodyYaw, float tickDelta)
    {
        if (!entity.isInPose(EntityPose.SLEEPING))
        {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bodyYaw));
        }

        if (entity.deathTime > 0)
        {
            float deathAngle = (entity.deathTime + tickDelta - 1F) / 20F * 1.6F;

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Math.min(MathHelper.sqrt(deathAngle), 1F) * 90F));
        }
    }
}
