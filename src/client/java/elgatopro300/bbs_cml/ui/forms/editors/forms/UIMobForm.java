package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.MobForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIMobFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIMobForm extends UIForm<MobForm>
{
    public UIMobForm()
    {
        super();

        this.defaultPanel = new UIMobFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_MOB_TITLE, Icons.MORPH);
        this.registerDefaultPanels();
    }
}