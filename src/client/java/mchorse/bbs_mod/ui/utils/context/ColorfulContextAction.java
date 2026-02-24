package elgatopro300.bbs_cml.ui.utils.context;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class ColorfulContextAction extends ContextAction
{
    public int color;

    public ColorfulContextAction(Icon icon, IKey label, Runnable runnable, int color)
    {
        super(icon, label, runnable);

        this.color = color;
    }

    @Override
    protected void renderBackground(UIContext context, int x, int y, int w, int h, boolean hover, boolean selected)
    {
        super.renderBackground(context, x, y, w, h, hover, selected);

        context.batcher.box(x, y, x + 2, y + h, Colors.A100 | this.color);
        context.batcher.gradientHBox(x + 2, y, x + 24, y + h, Colors.A25 | this.color, this.color);
    }
}
