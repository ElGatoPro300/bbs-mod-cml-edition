package elgatopro300.bbs_cml.ui.forms.editors.utils;

import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.renderers.FormRenderType;
import elgatopro300.bbs_cml.forms.renderers.FormRenderingContext;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIModelRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;

public class UIFormRenderer extends UIModelRenderer
{
    public Form form;

    @Override
    protected void renderUserModel(UIContext context)
    {
        if (this.form == null)
        {
            return;
        }

        FormRenderingContext formContext = new FormRenderingContext()
            .set(FormRenderType.PREVIEW, this.entity, new MatrixStack(), LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, context.getTransition())
            .camera(this.camera)
            .modelRenderer();

        FormUtilsClient.render(this.form, formContext);
    }
}