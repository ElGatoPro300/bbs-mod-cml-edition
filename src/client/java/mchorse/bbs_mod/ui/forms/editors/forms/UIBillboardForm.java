package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.BillboardForm;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIBillboardFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIBillboardForm extends UIForm<BillboardForm>
{
    private UIBillboardFormPanel billboardFormPanel;

    public UIBillboardForm()
    {
        super();

        this.billboardFormPanel = new UIBillboardFormPanel(this);
        this.defaultPanel = this.billboardFormPanel;

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_BILLBOARD_TITLE, Icons.MATERIAL);
        this.registerDefaultPanels();

        this.defaultPanel.keys().register(Keys.FORMS_PICK_TEXTURE, () ->
        {
            if (this.view != this.billboardFormPanel)
            {
                this.setPanel(this.billboardFormPanel);
            }

            this.billboardFormPanel.pick.clickItself();
        });
    }
}