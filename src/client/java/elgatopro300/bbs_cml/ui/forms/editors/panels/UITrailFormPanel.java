package elgatopro300.bbs_cml.ui.forms.editors.panels;

import elgatopro300.bbs_cml.forms.forms.TrailForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.forms.UIForm;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITexturePicker;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UITrailFormPanel extends UIFormPanel<TrailForm>
{
    public UIButton pick;
    public UITrackpad length;
    public UIToggle loop;
    public UIToggle paused;

    public UITrailFormPanel(UIForm editor)
    {
        super(editor);

        this.pick = new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_PICK_TEXTURE, (b) ->
        {
            UITexturePicker.open(this.getContext(), this.form.texture.get(), (l) -> this.form.texture.set(l));
        });
        this.length = new UITrackpad((v) -> this.form.length.set(v.floatValue()));
        this.loop = new UIToggle(UIKeys.FORMS_EDITORS_TRAIL_LOOP, (b) -> this.form.loop.set(b.getValue()));
        this.paused = new UIToggle(UIKeys.FORMS_EDITORS_VANILLA_PARTICLE_PAUSED, (b) -> this.form.paused.set(b.getValue()));

        this.options.add(this.pick, UI.label(UIKeys.FORMS_EDITORS_TRAIL_LENGTH), this.length, this.loop, this.paused);
    }

    @Override
    public void startEdit(TrailForm form)
    {
        super.startEdit(form);

        this.length.setValue(form.length.get());
        this.loop.setValue(form.loop.get());
        this.paused.setValue(form.paused.get());
    }
}