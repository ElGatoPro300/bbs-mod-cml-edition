package elgatopro300.bbs_cml.ui.dashboard.panels;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.overlay.UICRUDOverlayPanel;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public abstract class UICRUDDashboardPanel extends UISidebarDashboardPanel
{
    public UIIcon openOverlay;

    public final UICRUDOverlayPanel overlay;

    public UICRUDDashboardPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.overlay = this.createOverlayPanel();
        this.openOverlay = new UIIcon(Icons.MORE, (b) ->
        {
            UIOverlay.addOverlay(this.getContext(), this.overlay, 200, 0.9F);
        });

        this.iconBar.prepend(this.openOverlay);

        this.keys().register(Keys.OPEN_DATA_MANAGER, this.openOverlay::clickItself);
    }

    protected abstract UICRUDOverlayPanel createOverlayPanel();

    protected abstract IKey getTitle();

    public abstract void pickData(String id);
}