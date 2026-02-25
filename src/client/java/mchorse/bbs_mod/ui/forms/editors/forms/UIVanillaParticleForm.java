package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.VanillaParticleForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIVanillaParticleFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIVanillaParticleForm extends UIForm<VanillaParticleForm>
{
    public UIVanillaParticleForm()
    {
        super();

        this.defaultPanel = new UIVanillaParticleFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_VANILLA_PARTICLE_TITLE, Icons.PARTICLE);
        this.registerDefaultPanels();
    }
}