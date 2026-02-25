package elgatopro300.bbs_cml.ui.film.replays.overlays;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditor;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import elgatopro300.bbs_cml.utils.colors.Colors;

import java.util.Set;
import java.util.function.Consumer;

public class UIKeyframeSheetFilterOverlayPanel extends UIOverlayPanel
{
    public UIKeyframeSheetFilterOverlayPanel(Set<String> disabled, Set<String> keys)
    {
        super(UIKeys.FILM_REPLAY_FILTER_SHEETS_TITLE);

        UIScrollView scrollView = UI.scrollView(4, 6);

        scrollView.full(this.content);
        this.content.add(scrollView);

        for (String key : keys)
        {
            UIToggle toggle = new UICoolToggle(key, IKey.constant(key), (b) ->
            {
                if (disabled.contains(key))
                {
                    disabled.remove(key);
                }
                else
                {
                    disabled.add(key);
                }
            });

            toggle.h(20);
            toggle.setValue(!disabled.contains(key));
            scrollView.add(toggle);
        }
    }

    public static class UICoolToggle extends UIToggle
    {
        private String key;

        public UICoolToggle(String key, IKey label, Consumer<UIToggle> callback)
        {
            super(label, callback);

            this.key = key;
        }

        @Override
        protected void renderSkin(UIContext context)
        {
            int x = this.area.x;
            int y = this.area.y;
            int w = this.area.w;
            int h = this.area.h;
            Icon icon = UIReplaysEditor.getIcon(this.key);
            int color = UIReplaysEditor.getColor(key);

            context.batcher.box(x, y, x + 2, y + h, Colors.A100 | color);
            context.batcher.gradientHBox(x + 2, y, x + 24, y + h, Colors.A25 | color, color);
            context.batcher.icon(icon, x + 2, y + h / 2, 0F, 0.5F);

            this.area.x += 20;
            this.area.w -= 20;

            super.renderSkin(context);

            this.area.x = x;
            this.area.w = w;
        }
    }
}