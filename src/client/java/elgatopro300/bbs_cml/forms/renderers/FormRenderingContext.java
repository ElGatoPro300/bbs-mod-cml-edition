package elgatopro300.bbs_cml.forms.renderers;

import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.ui.framework.elements.utils.StencilMap;
import elgatopro300.bbs_cml.utils.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class FormRenderingContext
{
    public FormRenderType type;
    public IEntity entity;
    public MatrixStack stack;
    public int light;
    public int overlay;
    public float transition;
    public final Camera camera = new Camera();
    public StencilMap stencilMap;
    public boolean ui;
    public int color;
    public boolean modelRenderer;
    public boolean relative;
    public boolean isShadowPass;
    public Matrix4f viewMatrix;
    public Matrix4f projectionMatrix = new Matrix4f();
    public net.minecraft.client.render.VertexConsumerProvider vertexConsumers;

    public FormRenderingContext()
    {}

    public FormRenderingContext set(FormRenderType type, IEntity entity, MatrixStack stack, int light, int overlay, float transition)
    {
        this.type = type == null ? FormRenderType.ENTITY : type;
        this.entity = entity;
        this.stack = stack;
        this.light = light;
        this.overlay = overlay;
        this.transition = transition;
        this.stencilMap = null;
        this.ui = false;
        this.color = 0xffffffff;
        this.relative = false;
        this.isShadowPass = false;
        this.viewMatrix = null;

        return this;
    }

    public FormRenderingContext projection(Matrix4f projectionMatrix)
    {
        if (projectionMatrix != null)
        {
            this.projectionMatrix.set(projectionMatrix);
        }
        
        return this;
    }

    public FormRenderingContext camera(Camera camera)
    {
        this.camera.copy(camera);
        this.camera.updateView();

        return this;
    }

    public FormRenderingContext camera(net.minecraft.client.render.Camera camera)
    {
        net.minecraft.util.math.Vec3d pos = camera.getFocusedEntity() != null ? camera.getFocusedEntity().getCameraPosVec(0.0F) : net.minecraft.util.math.Vec3d.ofCenter(camera.getBlockPos());
        this.camera.position.set(pos.x, pos.y, pos.z);
        this.camera.rotation.set(MathUtils.toRad(-camera.getPitch()), MathUtils.toRad(camera.getYaw()), 0F);
        this.camera.fov = MathUtils.toRad(MinecraftClient.getInstance().options.getFov().getValue());
        this.camera.view.identity().rotate(camera.getRotation());

        return this;
    }

    public FormRenderingContext stencilMap(StencilMap stencilMap)
    {
        this.stencilMap = stencilMap;

        return this;
    }

    public FormRenderingContext inUI()
    {
        this.ui = true;

        return this;
    }

    public FormRenderingContext color(int color)
    {
        this.color = color;

        return this;
    }

    public FormRenderingContext modelRenderer()
    {
        this.modelRenderer = true;

        return this;
    }

    public FormRenderingContext consumers(net.minecraft.client.render.VertexConsumerProvider consumers)
    {
        this.vertexConsumers = consumers;

        return this;
    }

    public float getTransition()
    {
        return this.transition;
    }

    public boolean isPicking()
    {
        return this.stencilMap != null;
    }

    public int getPickingIndex()
    {
        return this.stencilMap == null ? -1 : this.stencilMap.objectIndex;
    }
}
