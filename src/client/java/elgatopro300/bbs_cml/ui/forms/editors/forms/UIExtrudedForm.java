package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.ExtrudedForm;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIExtrudedFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIExtrudedForm extends UIForm<ExtrudedForm>
{
    private UIExtrudedFormPanel extrudedFormPanel;

    public UIExtrudedForm()
    {
        super();

        this.extrudedFormPanel = new UIExtrudedFormPanel(this);
        this.defaultPanel = this.extrudedFormPanel;

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_EXTRUDED_TITLE, Icons.MATERIAL);
        this.registerDefaultPanels();

        this.defaultPanel.keys().register(Keys.FORMS_PICK_TEXTURE, () ->
        {
            if (this.view != this.extrudedFormPanel)
            {
                this.setPanel(this.extrudedFormPanel);
            }

            this.extrudedFormPanel.pick.clickItself();
        });
    }
}