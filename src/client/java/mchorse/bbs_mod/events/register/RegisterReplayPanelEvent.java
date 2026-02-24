package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.film.replays.overlays.UIReplaysOverlayPanel;

import java.util.function.Consumer;

public class RegisterReplayPanelEvent
{
    public void register(Consumer<UIReplaysOverlayPanel> consumer)
    {
        UIReplaysOverlayPanel.extensions.add(consumer);
    }
}
