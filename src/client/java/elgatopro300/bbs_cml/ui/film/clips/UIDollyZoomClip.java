package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.camera.clips.modifiers.DollyZoomClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIDollyZoomClip extends UIClip<DollyZoomClip>
{
    public UITrackpad focus;

    public UIDollyZoomClip(DollyZoomClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.focus = new UITrackpad((value) -> this.clip.focus.set(value.floatValue()));
        this.focus.tooltip(UIKeys.CAMERA_PANELS_FOCUS_DISTANCE);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.C_CLIP.get("bbs:dolly_zoom")), this.focus).marginTop(12));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.focus.setValue(this.clip.focus.get());
    }
}