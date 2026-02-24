package elgatopro300.bbs_cml.ui.framework.tooltips.styles;

import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class LightTooltipStyle extends TooltipStyle
{
    @Override
    public void renderBackground(UIContext context, Area area)
    {
        context.batcher.dropShadow(area.x, area.y, area.ex(), area.ey(), 4, Colors.A50, 0);
        area.render(context.batcher, Colors.WHITE);
    }

    @Override
    public int getTextColor()
    {
        return 0;
    }

    @Override
    public int getForegroundColor()
    {
        return 0;
    }
}