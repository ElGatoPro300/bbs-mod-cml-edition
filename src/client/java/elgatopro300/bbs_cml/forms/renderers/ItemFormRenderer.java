package elgatopro300.bbs_cml.forms.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.client.BBSRendering;
import elgatopro300.bbs_cml.client.BBSShaders;
import elgatopro300.bbs_cml.forms.CustomVertexConsumerProvider;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.forms.ItemForm;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.colors.Color;
import elgatopro300.bbs_cml.utils.joml.Vectors;
import net.minecraft.client.MinecraftClient;
import mchorse.bbs_mod.items.ItemDisplayMode;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ItemFormRenderer extends FormRenderer<ItemForm>
{
    public ItemFormRenderer(ItemForm form)
    {
        super(form);
    }

    private ItemDisplayContext convert(ItemDisplayMode mode)
    {
        switch (mode)
        {
            case THIRD_PERSON_LEFT_HAND: return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case THIRD_PERSON_RIGHT_HAND: return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            case FIRST_PERSON_LEFT_HAND: return ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            case FIRST_PERSON_RIGHT_HAND: return ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            case HEAD: return ItemDisplayContext.HEAD;
            case GUI: return ItemDisplayContext.GUI;
            case GROUND: return ItemDisplayContext.GROUND;
            case FIXED: return ItemDisplayContext.FIXED;
            default: return ItemDisplayContext.NONE;
        }
    }

    @Override
    public void renderInUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        // context.batcher.getContext().draw();

        CustomVertexConsumerProvider consumers = FormUtilsClient.getProvider();
        MatrixStack matrices = new MatrixStack(); // context.batcher.getContext().getMatrices();

        Matrix4f uiMatrix = ModelFormRenderer.getUIMatrix(context, x1, y1, x2, y2);

        matrices.push();
        MatrixStackUtils.multiply(matrices, uiMatrix);
        matrices.scale(this.form.uiScale.get(), this.form.uiScale.get(), this.form.uiScale.get());

        matrices.peek().getNormalMatrix().getScale(Vectors.EMPTY_3F);
        matrices.peek().getNormalMatrix().scale(1F / Vectors.EMPTY_3F.x, -1F / Vectors.EMPTY_3F.y, 1F / Vectors.EMPTY_3F.z);

        Color set = this.form.color.get();

        consumers.setSubstitute(BBSRendering.getColorConsumer(set));
        consumers.setUI(true);
        // Item rendering via ItemRenderer is disabled for 1.21.10 API migration
        consumers.draw();
        consumers.setUI(false);
        consumers.setSubstitute(null);

        matrices.pop();
    }

    @Override
    protected void render3D(FormRenderingContext context)
    {
        CustomVertexConsumerProvider consumers = FormUtilsClient.getProvider();
        int light = context.light;

        context.stack.push();

        if (context.isPicking())
        {
            CustomVertexConsumerProvider.hijackVertexFormat((layer) ->
            {
                this.setupTarget(context, BBSShaders.getPickerModelsProgram());
                // RenderSystem.setShader(BBSShaders.getPickerModelsProgram());
            });

            light = 0;
        }
        else
        {
            CustomVertexConsumerProvider.hijackVertexFormat((l) -> com.mojang.blaze3d.opengl.GlStateManager._enableBlend());
        }

        Color set = this.form.color.get();

        BlockFormRenderer.color.set(context.color);
        BlockFormRenderer.color.mul(set);

        consumers.setSubstitute(BBSRendering.getColorConsumer(BlockFormRenderer.color));
        // FIXME: Update renderItem signature for 1.21.5
        // MinecraftClient.getInstance().getItemRenderer().renderItem(this.convert(this.form.modelTransform.get()), context.stack, consumers, light, context.overlay, this.form.stack.get(), context.entity.getWorld(), 0);
        consumers.draw();
        consumers.setSubstitute(null);

        CustomVertexConsumerProvider.clearRunnables();

        context.stack.pop();

        // RenderSystem.enableDepthTest();
    }
}
