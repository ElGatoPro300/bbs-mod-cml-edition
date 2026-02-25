package elgatopro300.bbs_cml.ui.framework.tooltips;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;

public interface ITooltip
{
    public IKey getLabel();

    public void renderTooltip(UIContext context);
}
