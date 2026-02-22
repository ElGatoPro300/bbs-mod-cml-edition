package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.ItemForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIItemFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIItemForm extends UIForm<ItemForm>
{
    public UIItemForm()
    {
        super();

        this.defaultPanel = new UIItemFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_ITEM_TITLE, Icons.LINE);
        this.registerDefaultPanels();
    }
}