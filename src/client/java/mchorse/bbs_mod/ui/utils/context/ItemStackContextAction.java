package elgatopro300.bbs_cml.ui.utils.context;

import elgatopro300.bbs_cml.forms.CustomVertexConsumerProvider;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.utils.FontRenderer;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Colors;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ItemStackContextAction extends ContextAction
{
    public ItemStack stack = ItemStack.EMPTY;

    public ItemStackContextAction(ItemStack stack, IKey label, Runnable runnable)
    {
        super(Icons.NONE, label, runnable);

        this.stack = stack;
    }

    @Override
    public void render(UIContext context, FontRenderer font, int x, int y, int w, int h, boolean hover, boolean selected)
    {
        this.renderBackground(context, x, y, w, h, hover, selected);

        if (this.stack != null && !this.stack.isEmpty())
        {
            var matrices = context.batcher.getContext().getMatrices();
            CustomVertexConsumerProvider consumers = FormUtilsClient.getProvider();

            // matrices.push();
            consumers.setUI(true);
            context.batcher.getContext().drawItem(this.stack, x + 2, y + 2);
            context.batcher.getContext().drawStackOverlay(context.batcher.getFont().getRenderer(), this.stack, x + 2, y + 2);
            consumers.setUI(false);
            // matrices.pop();
        }

        context.batcher.text(this.label.get(), x + 22, y + (h - font.getHeight()) / 2 + 1, Colors.WHITE, false);
    }
}
