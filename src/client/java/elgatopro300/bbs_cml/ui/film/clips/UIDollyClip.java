package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.camera.clips.CameraClipContext;
import elgatopro300.bbs_cml.camera.clips.overwrite.DollyClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.clips.modules.UIAngleModule;
import elgatopro300.bbs_cml.ui.film.clips.modules.UIPointModule;
import elgatopro300.bbs_cml.ui.film.utils.UICameraUtils;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.context.UIInterpolationContextMenu;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.framework.tooltips.InterpolationTooltip;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIDollyClip extends UIClip<DollyClip>
{
    public UIPointModule point;
    public UIAngleModule angle;

    public UITrackpad distance;
    public UIIcon reverse;
    public UIButton interp;

    public UITrackpad yaw;
    public UITrackpad pitch;

    public UIDollyClip(DollyClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor);
        this.angle = new UIAngleModule(editor);
        this.distance = new UITrackpad((value) -> this.clip.distance.set(value.floatValue()));
        this.distance.tooltip(UIKeys.CAMERA_PANELS_DOLLY_DISTANCE);
        this.reverse = new UIIcon(Icons.REVERSE, (b) -> this.reverse());
        this.reverse.tooltip(UIKeys.CAMERA_PANELS_DOLLY_REVERSE);
        this.yaw = new UITrackpad((value) -> this.clip.yaw.set(value.floatValue()));
        this.yaw.tooltip(UIKeys.CAMERA_PANELS_DOLLY_YAW);
        this.pitch = new UITrackpad((value) -> this.clip.pitch.set(value.floatValue()));
        this.pitch.tooltip(UIKeys.CAMERA_PANELS_DOLLY_PITCH);

        this.interp = new UIButton(UIKeys.CAMERA_PANELS_INTERPOLATION, (b) ->
        {
            this.getContext().replaceContextMenu(new UIInterpolationContextMenu(this.clip.interp));
        });
        this.interp.tooltip(new InterpolationTooltip(1F, 0.5F, () -> this.clip.interp));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_DOLLY_TITLE), UI.row(0, 0, 20, this.distance, this.reverse)).marginTop(12));
        this.panels.add(this.yaw, this.pitch, this.interp);
        this.panels.add(this.point.marginTop(12), this.angle.marginTop(6));
        this.panels.context((menu) -> UICameraUtils.positionContextMenu(menu, this.editor, this.clip.position));
    }

    private void reverse()
    {
        Position position = new Position();

        this.clip.applyLast(new CameraClipContext(), position);
        this.clip.position.set(position);
        this.clip.distance.set(-this.clip.distance.get());

        this.fillData();
    }

    @Override
    public void editClip(Position position)
    {
        this.clip.position.set(position);
        this.clip.yaw.set(position.angle.yaw);
        this.clip.pitch.set(position.angle.pitch);

        super.editClip(position);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.point.fill(this.clip.position.getPoint());
        this.angle.fill(this.clip.position.getAngle());

        this.yaw.setValue(this.clip.yaw.get());
        this.pitch.setValue(this.clip.pitch.get());
        this.distance.setValue(this.clip.distance.get());
    }
}