package elgatopro300.bbs_cml.ui.film.clips.actions;

import elgatopro300.bbs_cml.actions.types.blocks.BreakBlockActionClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIBreakBlockActionClip extends UIActionClip<BreakBlockActionClip>
{
    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad z;
    public UITrackpad progress;

    public UIBreakBlockActionClip(BreakBlockActionClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.x = new UITrackpad((v) -> this.editor.editMultiple(this.clip.x, (x) -> x.set(v.intValue())));
        this.x.integer();
        this.y = new UITrackpad((v) -> this.editor.editMultiple(this.clip.y, (y) -> y.set(v.intValue())));
        this.y.integer();
        this.z = new UITrackpad((v) -> this.editor.editMultiple(this.clip.z, (z) -> z.set(v.intValue())));
        this.z.integer();
        this.progress = new UITrackpad((v) -> this.editor.editMultiple(this.clip.progress, (progress) -> progress.set(v.intValue())));
        this.progress.integer();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.label(UIKeys.ACTIONS_BLOCK_POSITION).marginTop(12));
        this.panels.add(UI.row(this.x, this.y, this.z));
        this.panels.add(UI.label(UIKeys.ACTIONS_BLOCK_PROGRESS).marginTop(12), this.progress);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.x.setValue(this.clip.x.get());
        this.y.setValue(this.clip.y.get());
        this.z.setValue(this.clip.z.get());
        this.progress.setValue(this.clip.progress.get());
    }
}
