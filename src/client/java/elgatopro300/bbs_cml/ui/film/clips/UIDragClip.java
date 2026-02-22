package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.camera.clips.modifiers.DragClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.clips.widgets.UIBitToggle;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIDragClip extends UIClip<DragClip>
{
    public UIToggle deterministic;
    public UITrackpad factor;
    public UITrackpad rate;
    public UIBitToggle active;

    public UIDragClip(DragClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.deterministic = new UIToggle(UIKeys.CAMERA_PANELS_DETERMINISTIC, (b) ->
        {
            this.clip.deterministic.set(b.getValue());
            this.clip.resetCache();
        });
        this.deterministic.tooltip(UIKeys.CAMERA_PANELS_DETERMINISTIC_TOOLTIP);

        this.factor = new UITrackpad((value) -> this.clip.factor.set(value.floatValue()));
        this.factor.limit(this.clip.factor).values(0.05F, 0.01F, 0.2F).increment(0.1F).tooltip(UIKeys.CAMERA_PANELS_FACTOR_TOOLTIP);

        this.rate = new UITrackpad((value) -> this.clip.rate.set(value.intValue()));
        this.rate.limit(this.clip.rate).tooltip(UIKeys.CAMERA_PANELS_RATE_TOOLTIP);

        this.active = new UIBitToggle((value) ->
        {
            this.clip.active.set(value);
            this.clip.resetCache();
        }).all();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.C_CLIP.get("bbs:drag")), this.deterministic).marginTop(12));
        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_FACTOR), this.factor).marginTop(6), this.rate, this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.deterministic.setValue(this.clip.deterministic.get());
        this.factor.setValue(this.clip.factor.get());
        this.rate.setValue(this.clip.rate.get());
        this.active.setValue(this.clip.active.get());
    }
}