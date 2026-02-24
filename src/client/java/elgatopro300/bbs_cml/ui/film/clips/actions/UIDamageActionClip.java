package elgatopro300.bbs_cml.ui.film.clips.actions;

import elgatopro300.bbs_cml.actions.types.DamageActionClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIDamageActionClip extends UIActionClip<DamageActionClip>
{
    public UITrackpad damage;

    public UIDamageActionClip(DamageActionClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.damage = new UITrackpad((v) -> this.editor.editMultiple(this.clip.damage, (damage) -> damage.set(v.floatValue())));
        this.damage.limit(0F);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.label(UIKeys.ACTIONS_ATTACK_DAMAGE).marginTop(12), this.damage);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.damage.setValue(this.clip.damage.get());
    }
}