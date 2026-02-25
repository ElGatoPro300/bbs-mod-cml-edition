package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.forms.ParticleForm;
import elgatopro300.bbs_cml.forms.renderers.ParticleFormRenderer;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITexturePicker;

public class UIParticleForm extends UIForm<ParticleForm>
{
    public UIParticleForm()
    {
        super();

        UIElement button = new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_PICK_TEXTURE, (b) ->
        {
            Link texture = this.form.texture.get();
            ParticleEmitter emitter = ((ParticleFormRenderer) FormUtilsClient.getRenderer(this.form)).getEmitter();

            if (emitter != null && texture == null)
            {
                texture = emitter.scheme.texture;
            }

            UITexturePicker.open(this.getContext(), texture, (l) -> this.form.texture.set(l));
        }).marginBottom(6);

        this.registerDefaultPanels();

        this.defaultPanel = this.panels.get(this.panels.size() - 1);
        this.defaultPanel.options.prepend(button);

        this.defaultPanel.keys().register(Keys.FORMS_PICK_TEXTURE, button::clickItself);
    }
}