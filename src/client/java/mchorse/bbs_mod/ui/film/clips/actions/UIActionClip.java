package elgatopro300.bbs_cml.ui.film.clips.actions;

import elgatopro300.bbs_cml.actions.types.ActionClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.clips.UIClip;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;

public abstract class UIActionClip <T extends ActionClip> extends UIClip<T>
{
    public UITrackpad frequency;

    public UIActionClip(T clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.frequency = new UITrackpad((v) -> this.editor.editMultiple(this.clip.frequency, (frequency) -> frequency.set(v.intValue())));
        this.frequency.limit(0).integer();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.label(UIKeys.ACTIONS_FREQUENCY).marginTop(6), this.frequency);
    }

    @Override
    protected void addEnvelopes()
    {}

    @Override
    public void fillData()
    {
        super.fillData();

        this.frequency.setValue(this.clip.frequency.get());
    }
}