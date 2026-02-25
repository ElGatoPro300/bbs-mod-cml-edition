package elgatopro300.bbs_cml.ui.utils;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.ui.ContentType;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.renderers.InputRenderer;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.interps.Interpolations;
import elgatopro300.bbs_cml.utils.interps.Lerps;

import java.util.Collection;
import java.util.function.Consumer;

public class UIDataUtils
{
    public static void requestNames(ContentType type, Consumer<Collection<String>> consumer)
    {
        type.getRepository().requestKeys(consumer);
    }

    public static void renderRightClickHere(UIContext context, Area area)
    {
        int primary = BBSSettings.primaryColor.get();
        double ticks = context.getTickTransition() % 80D;
        double factor = Math.abs(ticks / 80D * 2 - 1F);

        factor = Interpolations.EXP_INOUT.interpolate(0, 1, factor);

        double factor2 = Lerps.envelope(ticks, 37, 40, 40, 43);

        factor2 = Interpolations.CUBIC_OUT.interpolate(0, 1, factor2);

        int offset = (int) (factor * 70 + factor2 * 2);

        context.batcher.dropCircleShadow(area.mx(), area.my() + (int) (factor * 70), 16, 0, 16, Colors.A50 | primary, primary);
        InputRenderer.renderMouseButtons(context.batcher, area.mx() - 6, area.my() - 8 + offset, 0, false, factor2 > 0, false, false);

        String label = UIKeys.GENERAL_RIGHT_CLICK.get();
        int w = (int) (area.w / 1.1F);
        int color = Colors.mulRGB(0x444444, 1 - (float) factor);

        context.batcher.wallText(label, area.mx() - w / 2, area.my() - 20, color, w, 12, 0.5F, 1);

        context.batcher.gradientVBox(area.x, area.my() + 20, area.ex(), area.my() + 40, 0, Colors.A100);
        context.batcher.box(area.x, area.my() + 40, area.ex(), area.my() + 90, Colors.A100);
    }
}