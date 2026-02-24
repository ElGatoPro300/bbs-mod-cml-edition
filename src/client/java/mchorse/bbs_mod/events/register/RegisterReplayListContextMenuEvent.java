package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.film.replays.UIReplayList;
import elgatopro300.bbs_cml.ui.utils.context.ContextMenuManager;

import java.util.function.BiConsumer;

public class RegisterReplayListContextMenuEvent
{
    public void register(BiConsumer<UIReplayList, ContextMenuManager> consumer)
    {
        UIReplayList.extensions.add(consumer);
    }
}
