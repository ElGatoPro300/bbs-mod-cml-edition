package elgatopro300.bbs_cml.forms.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.entities.StubEntity;
import elgatopro300.bbs_cml.forms.forms.AnchorForm;
import elgatopro300.bbs_cml.graphics.texture.Texture;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.joml.Vectors;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class AnchorFormRenderer extends FormRenderer<AnchorForm>
{
    public static final Link ANCHOR_PREVIEW = Link.assets("textures/anchor.png");

    private IEntity entity = new StubEntity();

    public AnchorFormRenderer(AnchorForm form)
    {
        super(form);
    }

    @Override
    protected void renderInUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        if (this.form.parts.getAll().isEmpty())
        {
            Texture texture = context.render.getTextures().getTexture(ANCHOR_PREVIEW);

            int w = texture.width;
            int h = texture.height;
            int x = (x1 + x2) / 2;
            int y = (y1 + y2) / 2;

            context.batcher.fullTexturedBox(texture, x - w / 2, y - h / 2, w, h);
        }
        else
        {
            MatrixStack stack = context.batcher.getContext().getMatrices();
            Matrix4f uiMatrix = ModelFormRenderer.getUIMatrix(context, x1, y1, x2, y2);

            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            stack.push();

            this.applyTransforms(uiMatrix, context.getTransition());
            MatrixStackUtils.multiply(stack, uiMatrix);
            /* Why? I don't know, because fuck you */
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180F));
            stack.peek().getNormalMatrix().getScale(Vectors.EMPTY_3F);
            stack.peek().getNormalMatrix().scale(1F / Vectors.EMPTY_3F.x, -1F / Vectors.EMPTY_3F.y, 1F / Vectors.EMPTY_3F.z);

            this.renderBodyParts(new FormRenderingContext()
                .set(FormRenderType.ENTITY, this.entity, stack, LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, context.getTransition())
                .inUI());

            stack.pop();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);
        }
    }
}