package elgatopro300.bbs_cml.forms.renderers;

import elgatopro300.bbs_cml.client.BBSRendering;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.forms.BodyPart;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.renderers.utils.MatrixCache;
import elgatopro300.bbs_cml.settings.values.core.ValueTransform;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.utils.FontRenderer;
import elgatopro300.bbs_cml.ui.utils.keys.KeyCodes;
import elgatopro300.bbs_cml.utils.MathUtils;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.StringUtils;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.interps.Lerps;
import elgatopro300.bbs_cml.utils.pose.Transform;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class FormRenderer <T extends Form>
{
    private static boolean suppressFormDisplayName;

    public static void setSuppressFormDisplayName(boolean suppress)
    {
        suppressFormDisplayName = suppress;
    }

    protected T form;

    public FormRenderer(T form)
    {
        this.form = form;
    }

    public T getForm()
    {
        return this.form;
    }

    public List<String> getBones()
    {
        return Collections.emptyList();
    }

    public final void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        this.renderInUI(context, x1, y1, x2, y2);

        FontRenderer font = context.batcher.getFont();
        String name = this.form.name.get();

        if (!suppressFormDisplayName && !name.isEmpty())
        {
            name = font.limitToWidth(name, x2 - x1 - 3);

            int w = font.getWidth(name);

            context.batcher.textCard(name, (x2 + x1 - w) / 2, y1 + 6, Colors.WHITE, Colors.ACTIVE | Colors.A50);
        }

        int keybind = this.form.hotkey.get();

        if (keybind > 0)
        {
            name = KeyCodes.getName(keybind);
            name = font.limitToWidth(name, x2 - x1 - 3);

            int w = font.getWidth(name);

            context.batcher.textCard(name, (x2 + x1 - w) / 2, y2 - 6 - font.getHeight(), Colors.WHITE, Colors.A50);
        }
    }

    protected abstract void renderInUI(UIContext context, int x1, int y1, int x2, int y2);

    public boolean renderArm(MatrixStack matrices, int light, AbstractClientPlayerEntity player, Hand hand)
    {
        return false;
    }

    public final void render(FormRenderingContext context)
    {
        if (!this.form.shaderShadow.get() && BBSRendering.isIrisShadowPass())
        {
            return;
        }

        this.form.applyStates(context.transition);

        int light = context.light;
        boolean visible = this.form.visible.get();

        if (!visible)
        {
            return;
        }

        boolean isPicking = context.stencilMap != null;

        context.stack.push();
        this.applyTransforms(context.stack, false, context.getTransition());

        float lf = 1F - MathUtils.clamp(this.form.lighting.get(), 0F, 1F);
        int u = context.light & '\uffff';
        int v = context.light >> 16 & '\uffff';

        u = (int) Lerps.lerp(u, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, lf);
        context.light = u | v << 16;

        this.render3D(context);

        if (isPicking)
        {
            this.updateStencilMap(context);
        }

        this.renderBodyParts(context);

        context.stack.pop();

        context.light = light;

        this.form.unapplyStates();
    }

    protected void applyTransforms(MatrixStack stack, boolean origin, float transition)
    {
        Transform transform = this.createTransform();

        if (origin)
        {
            stack.translate(transform.translate.x, transform.translate.y, transform.translate.z);
        }
        else
        {
            MatrixStackUtils.applyTransform(stack, transform);
        }
    }

    protected void applyTransforms(Matrix4f matrix, float transition)
    {
        matrix.mul(this.createTransform().createMatrix());
    }

    protected Transform createTransform()
    {
        Transform transform = new Transform();

        transform.copy(this.form.transform.get());
        this.applyTransform(transform, this.form.transformOverlay.get());

        for (ValueTransform t : this.form.additionalTransforms)
        {
            this.applyTransform(transform, t.get());
        }

        return transform;
    }

    private void applyTransform(Transform transform, Transform overlay)
    {
        transform.translate.add(overlay.translate);
        transform.scale.add(overlay.scale).sub(1, 1, 1);
        transform.rotate.add(overlay.rotate);
        transform.rotate2.add(overlay.rotate2);
        transform.pivot.add(overlay.pivot);
    }

    protected Supplier<ShaderProgram> getShader(FormRenderingContext context, Supplier<ShaderProgram> normal, Supplier<ShaderProgram> picking)
    {
        if (context.isPicking())
        {
            ShaderProgram program = picking.get();

            if (program == null)
            {
                return normal;
            }

            this.setupTarget(context, program);

            return () -> program;
        }

        return normal;
    }

    protected void setupTarget(FormRenderingContext context, ShaderProgram program)
    {
        if (program == null)
        {
            return;
        }

        GlUniform target = program.getUniform("Target");

        if (target != null)
        {
            int pickingIndex = context.getPickingIndex();

            target.set(pickingIndex);
        }
    }

    protected void updateStencilMap(FormRenderingContext context)
    {
        context.stencilMap.addPicking(this.form);
    }

    protected void render3D(FormRenderingContext context)
    {}

    public void renderBodyParts(FormRenderingContext context)
    {
        for (BodyPart part : this.form.parts.getAllTyped())
        {
            this.renderBodyPart(part, context);
        }
    }

    protected void renderBodyPart(BodyPart part, FormRenderingContext context)
    {
        IEntity oldEntity = context.entity;

        context.entity = part.useTarget.get() ? oldEntity : part.getEntity();

        if (part.getForm() != null)
        {
            context.stack.push();
            MatrixStackUtils.applyTransform(context.stack, part.transform.get());

            FormUtilsClient.render(part.getForm(), context);

            context.stack.pop();
        }

        context.entity = oldEntity;
    }

    public MatrixCache collectMatrices(IEntity entity, float transition)
    {
        MatrixCache map = new MatrixCache();
        MatrixStack stack = new MatrixStack();

        this.collectMatrices(entity, stack, map, "", transition);

        return map;
    }

    public void collectMatrices(IEntity entity, MatrixStack stack, MatrixCache matrices, String prefix, float transition)
    {
        Matrix4f mm = new Matrix4f();
        Matrix4f oo = new Matrix4f();

        stack.push();
        this.applyTransforms(stack, true, transition);
        oo.set(stack.peek().getPositionMatrix());
        stack.pop();

        stack.push();
        this.applyTransforms(stack, false, transition);
        mm.set(stack.peek().getPositionMatrix());

        matrices.put(prefix, mm, oo);

        int i = 0;

        for (BodyPart part : this.form.parts.getAllTyped())
        {
            Form form = part.getForm();

            if (form != null)
            {
                stack.push();
                MatrixStackUtils.applyTransform(stack, part.transform.get());

                FormUtilsClient.getRenderer(form).collectMatrices(entity, stack, matrices, StringUtils.combinePaths(prefix, String.valueOf(i)), transition);

                stack.pop();
            }

            i += 1;
        }

        stack.pop();
    }
}
