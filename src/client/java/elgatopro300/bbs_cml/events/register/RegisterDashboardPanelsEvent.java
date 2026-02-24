package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;

public class RegisterDashboardPanelsEvent
{
    public final UIDashboard dashboard;

    public RegisterDashboardPanelsEvent(UIDashboard dashboard)
    {
        this.dashboard = dashboard;
    }
}
