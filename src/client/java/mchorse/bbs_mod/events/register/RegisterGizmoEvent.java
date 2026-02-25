package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.utils.Gizmo;

public class RegisterGizmoEvent
{
    public void register(int index, Gizmo.IGizmoHandler handler)
    {
        Gizmo.INSTANCE.register(index, handler);
    }
}
