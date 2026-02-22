package elgatopro300.bbs_cml.ui.film.clips.actions;

import elgatopro300.bbs_cml.actions.types.AttackActionClip;
import elgatopro300.bbs_cml.actions.types.SwipeActionClip;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;

public class UISwipeActionClip extends UIActionClip<SwipeActionClip>
{
    public UISwipeActionClip(SwipeActionClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }
}