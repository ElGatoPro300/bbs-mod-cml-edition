package elgatopro300.bbs_cml.ui.supporters;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.UIDashboardPanel;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.utils.Batcher2D;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.UIUtils;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.Direction;
import elgatopro300.bbs_cml.utils.colors.Colors;

import java.util.function.Supplier;

public class UISupportersPanel extends UIDashboardPanel
{
    public UIElement ccSupporters;
    public UIElement superSupporters;
    public UIElement bbsEarlyAccessSupporters;
    public UIElement cmlSupporters;
    public UIElement cmlDevelopers;
    public UIElement specialThanksSupporters;

    private Supporters supporters = new Supporters();

    public UISupportersPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.supporters.setup();

        this.ccSupporters = new UIElement();
        this.ccSupporters.grid(5).items(3);
        this.superSupporters = new UIElement();
        this.superSupporters.grid(5).items(3);
        this.bbsEarlyAccessSupporters = new UIElement();
        this.bbsEarlyAccessSupporters.grid(5).items(3);
        this.cmlSupporters = new UIElement();
        this.cmlSupporters.grid(5).items(3);
        this.cmlDevelopers = new UIElement();
        this.cmlDevelopers.grid(5).items(3);
        this.specialThanksSupporters = new UIElement();
        this.specialThanksSupporters.grid(5).items(3);

        UIScrollView scrollView = UI.scrollView(0, 0);
        UIElement column = UI.column(5, 10);

        scrollView.full(this);

        /* Resources */
        Supplier<Integer> color = () -> BBSSettings.primaryColor(Colors.A50);
        Supplier<Integer> orangeColor = () -> 0xFF_FF8C00; // Orange color for CML section
        Supplier<Integer> purpleColor = () -> 0xFF_9932CC; // Purple color for Developers section
        Supplier<Integer> blueColor = () -> 0xFF_1E90FF; // Blue color for Special Thanks section

        /* column.add(UI.label(UIKeys.SUPPORTERS_GRATITUDE)); */
        column.add(UI.label(UIKeys.SUPPORTERS_CML_INTRO).marginTop(6));
        column.add(UI.label(UIKeys.SUPPORTERS_CML_DEVELOPERS).background(purpleColor).marginTop(12).marginBottom(6));
        column.add(this.cmlDevelopers);
        column.add(UI.label(UIKeys.SUPPORTERS_CML_ANIMATORS).background(orangeColor).marginTop(6).marginBottom(6));
        column.add(this.cmlSupporters);
        column.add(UI.label(UIKeys.SUPPORTERS_SPECIAL_THANKS).background(blueColor).marginTop(12).marginBottom(6));
        column.add(this.specialThanksSupporters);
        column.add(UI.label(UIKeys.SUPPORTERS_CC).background(color).marginTop(12).marginBottom(6));
        column.add(this.ccSupporters);
        column.add(UI.label(UIKeys.SUPPORTERS_SUPER_SUPPORTERS).background(color).marginTop(12).marginBottom(6));
        column.add(this.superSupporters);
        column.add(UI.label(UIKeys.SUPPORTERS_EARLY_ACCESS).background(color).marginTop(12).marginBottom(6));
        column.add(this.bbsEarlyAccessSupporters.marginBottom(12));
        column.w(500);

        UIElement row = UI.row(0, 0, new UIElement(), column, new UIElement());

        /* Fill in */

        for (Supporter supporter : this.supporters.getCMLDevelopers())
        {
            this.cmlDevelopers.add(this.createSupporter(supporter));
        }

        for (Supporter supporter : this.supporters.getCMLSupporters())
        {
            this.cmlSupporters.add(this.createSupporter(supporter));
        }

        for (Supporter supporter : this.supporters.getSpecialThanksSupporters())
        {
            this.specialThanksSupporters.add(this.createSupporter(supporter));
        }

        for (Supporter supporter : this.supporters.getCCSupporters())
        {
            this.ccSupporters.add(this.createSupporter(supporter));
        }

        for (Supporter supporter : this.supporters.getSuperSupporters())
        {
            this.superSupporters.add(this.createSupporter(supporter));
        }

        for (Supporter supporter : this.supporters.getBBSEarlyAccessSupporters())
        {
            this.bbsEarlyAccessSupporters.add(this.createSupporter(supporter));
        }

        scrollView.add(row);

        UIIcon tutorials = new UIIcon(Icons.HELP, (b) -> UIUtils.openWebLink(UIKeys.SUPPORTERS_TUTORIALS_LINK.get()));
        UIIcon community = new UIIcon(Icons.USER, (b) -> UIUtils.openWebLink(UIKeys.SUPPORTERS_COMMUNITY_LINK.get()));
        UIIcon wiki = new UIIcon(Icons.FILE, (b) -> UIUtils.openWebLink(UIKeys.SUPPORTERS_WIKI_LINK.get()));
        UIIcon donate = new UIIcon(Icons.HEART_ALT, (b) -> UIUtils.openWebLink(UIKeys.SUPPORTERS_DONATE_LINK.get()));
        UIElement icons = UI.column(0, tutorials, community, wiki, donate);

        tutorials.tooltip(UIKeys.SUPPORTERS_TUTORIALS, Direction.RIGHT);
        community.tooltip(UIKeys.SUPPORTERS_COMMUNITY, Direction.RIGHT);
        wiki.tooltip(UIKeys.SUPPORTERS_WIKI, Direction.RIGHT);
        donate.tooltip(UIKeys.SUPPORTERS_DONATE, Direction.RIGHT);
        icons.relative(this).w(20).column(0).vertical().stretch();

        this.add(scrollView, icons);
    }

    public UIElement createSupporter(Supporter supporter)
    {
        if (supporter.hasOnlyName())
        {
            return UI.label(IKey.constant(supporter.name), Batcher2D.getDefaultTextRenderer().getHeight() + 4).labelAnchor(0F, 0.5F);
        }
        else if (supporter.hasNoBanner())
        {
            return UISupporterBanner.createLinkEntry(supporter);
        }

        return new UISupporterBanner(supporter);
    }
}