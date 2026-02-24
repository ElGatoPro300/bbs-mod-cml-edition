package elgatopro300.bbs_cml.ui.framework.tooltips.styles;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class DarkTooltipStyle extends TooltipStyle
{
    @Override
    public void renderBackground(UIContext context, Area area)
    {
        int color = BBSSettings.primaryColor.get();

        context.batcher.dropShadow(area.x, area.y, area.ex(), area.ey(), 6, Colors.A25 + color, color);
        area.render(context.batcher, Colors.A100);
    }

    @Override
    public int getTextColor()
    {
        return Colors.WHITE;
    }

    @Override
    public int getForegroundColor()
    {
        return BBSSettings.primaryColor.get();
    }
}