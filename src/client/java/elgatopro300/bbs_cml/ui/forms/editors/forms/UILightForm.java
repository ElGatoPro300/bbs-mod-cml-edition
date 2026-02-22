package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.LightForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UILightFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UILightForm extends UIForm<LightForm>
{
    public UILightForm()
    {
        super();

        this.defaultPanel = new UILightFormPanel(this);

        this.registerPanel(this.defaultPanel, IKey.raw("Light"), Icons.LIGHT);
        this.registerDefaultPanels();
    }
}

