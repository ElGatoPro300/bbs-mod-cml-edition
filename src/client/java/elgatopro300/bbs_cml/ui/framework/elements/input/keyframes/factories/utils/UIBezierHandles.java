package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories.utils;

import elgatopro300.bbs_cml.camera.utils.TimeUtils;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

public class UIBezierHandles
{
    private UITrackpad lx;
    private UITrackpad ly;
    private UITrackpad rx;
    private UITrackpad ry;

    private Keyframe<?> keyframe;

    public UIBezierHandles(Keyframe<?> keyframe)
    {
        this.keyframe = keyframe;

        this.lx = new UITrackpad((v) -> BaseValue.edit(this.keyframe, (kf) -> kf.lx = (float) TimeUtils.fromTime(v.floatValue())));
        this.ly = new UITrackpad((v) -> BaseValue.edit(this.keyframe, (kf) -> kf.ly = v.floatValue()));
        this.rx = new UITrackpad((v) -> BaseValue.edit(this.keyframe, (kf) -> kf.rx = (float) TimeUtils.fromTime(v.floatValue())));
        this.ry = new UITrackpad((v) -> BaseValue.edit(this.keyframe, (kf) -> kf.ry = v.floatValue()));
        this.lx.setValue(TimeUtils.toTime(this.keyframe.lx));
        this.ly.setValue(this.keyframe.ly);
        this.rx.setValue(TimeUtils.toTime(this.keyframe.rx));
        this.ry.setValue(this.keyframe.ry);
    }

    public UIElement createColumn()
    {
        return UI.column(
            UI.row(new UIIcon(Icons.LEFT_HANDLE, null).tooltip(UIKeys.KEYFRAMES_LEFT_HANDLE), this.lx, this.ly),
            UI.row(new UIIcon(Icons.RIGHT_HANDLE, null).tooltip(UIKeys.KEYFRAMES_RIGHT_HANDLE), this.rx, this.ry)
        );
    }

    public void update()
    {
        this.lx.setValue(TimeUtils.toTime(this.keyframe.lx));
        this.ly.setValue(this.keyframe.ly);
        this.rx.setValue(TimeUtils.toTime(this.keyframe.rx));
        this.ry.setValue(this.keyframe.ry);
    }
}