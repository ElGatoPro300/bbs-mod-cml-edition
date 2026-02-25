package elgatopro300.bbs_cml.ui.film.clips.renderer;

import elgatopro300.bbs_cml.ui.film.UIClips;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.utils.clips.Clip;

public interface IUIClipRenderer <T extends Clip>
{
    public void renderClip(UIContext context, UIClips clips, T clip, Area area, boolean selected, boolean current);
}