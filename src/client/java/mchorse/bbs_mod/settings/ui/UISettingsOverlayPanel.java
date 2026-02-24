package elgatopro300.bbs_cml.settings.ui;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.l10n.L10n;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.settings.Settings;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlayPanel;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UILabel;
import elgatopro300.bbs_cml.ui.utils.ScrollDirection;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.interps.Interpolations;
import elgatopro300.bbs_cml.utils.Direction;
import elgatopro300.bbs_cml.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class UISettingsOverlayPanel extends UIOverlayPanel
{
    public UIScrollView options;
    public UITextbox search;

    private Settings settings;
    private UIIcon currentButton;

    public UISettingsOverlayPanel()
    {
        super(UIKeys.CONFIG_TITLE);

        this.options = new UIScrollView(ScrollDirection.VERTICAL);
        this.options.scroll.scrollSpeed = 51;

        this.options.full(this.content);
        this.options.column().scroll().vertical().stretch().padding(10).height(20);

        this.search = new UITextbox(100, (str) -> this.refresh());
        this.search.placeholder(UIKeys.GENERAL_SEARCH);
        this.search.h(20);

        for (Settings settings : BBSMod.getSettings().modules.values())
        {
            UIIcon icon = new UIIcon(settings.icon, (b) ->
            {
                this.selectConfig(settings.getId(), b);
            });

            icon.tooltip(L10n.lang(UIValueFactory.getTitleKey(settings)), Direction.LEFT);
            this.icons.add(icon);
        }

        this.add(this.options);
        this.selectConfig("bbs", this.icons.getChildren(UIIcon.class).get(1));
        this.markContainer();
    }

    public void selectConfig(String mod, UIIcon currentButton)
    {
        this.settings = BBSMod.getSettings().modules.get(mod);
        this.currentButton = currentButton;

        this.refresh();
    }

    public void refresh()
    {
        if (this.settings == null)
        {
            return;
        }

        this.options.removeAll();
        this.options.add(this.search.marginBottom(10));

        boolean first = true;
        String query = this.search.getText().trim().toLowerCase();

        for (ValueGroup category : this.settings.categories.values())
        {
            if (!category.isVisible())
            {
                continue;
            }

            String catTitleKey = UIValueFactory.getCategoryTitleKey(category);
            String catTooltipKey = UIValueFactory.getCategoryTooltipKey(category);
            boolean categoryMatches = query.isEmpty() || this.matchesQuery(query,
                L10n.lang(catTitleKey).get(),
                L10n.lang(catTooltipKey).get(),
                category.getId()
            );

            UILabel label = UI.label(L10n.lang(catTitleKey)).labelAnchor(0, 1).background(() -> BBSSettings.primaryColor(Colors.A50));
            List<UIElement> options = new ArrayList<>();

            label.tooltip(L10n.lang(catTooltipKey), Direction.BOTTOM);

            for (BaseValue value : category.getAll())
            {
                if (!value.isVisible())
                {
                    continue;
                }
                boolean valueMatches = categoryMatches || query.isEmpty() || this.matchesQuery(query,
                    L10n.lang(UIValueFactory.getValueLabelKey(value)).get(),
                    L10n.lang(UIValueFactory.getValueCommentKey(value)).get(),
                    value.getId()
                );

                if (!valueMatches)
                {
                    continue;
                }

                /* Populate interpolation labels for default interpolation setting on client side */
                if (value == BBSSettings.defaultInterpolation)
                {
                    try
                    {
                        java.util.List<IKey> interpKeys = new java.util.ArrayList<>();

                        for (String k : Interpolations.MAP.keySet())
                        {
                            interpKeys.add(elgatopro300.bbs_cml.ui.UIKeys.C_INTERPOLATION.get(k));
                        }

                        if (value instanceof ValueInt)
                        {
                            ((ValueInt) value).modes(interpKeys.toArray(new IKey[0]));
                        }
                    }
                    catch (Throwable ignored) {}
                }

                if (value == BBSSettings.editorReplayHudPosition)
                {
                    if (value instanceof ValueInt)
                    {
                        ((ValueInt) value).modes(
                            L10n.lang("bbs.config.display.replay_hud_position.top_left"),
                            L10n.lang("bbs.config.display.replay_hud_position.top_right"),
                            L10n.lang("bbs.config.display.replay_hud_position.bottom_left"),
                            L10n.lang("bbs.config.display.replay_hud_position.bottom_right")
                        );
                    }
                }

                List<UIElement> elements = UIValueMap.create(value, this);

                for (UIElement element : elements)
                {
                    options.add(element);
                }
            }

            if (options.isEmpty())
            {
                continue;
            }

            UIElement firstContainer = UI.column(5, 0, 20, label, options.remove(0)).marginTop(first ? 0 : 24);

            this.options.add(firstContainer);

            for (UIElement element : options)
            {
                this.options.add(element);
            }

            first = false;
        }

        this.resize();
    }

    private boolean matchesQuery(String query, String... values)
    {
        if (query.isEmpty())
        {
            return true;
        }

        for (String value : values)
        {
            if (value != null && value.toLowerCase().contains(query))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        if (this.currentButton != null)
        {
            this.currentButton.area.render(context.batcher, BBSSettings.primaryColor(Colors.A100));
        }
    }
}
