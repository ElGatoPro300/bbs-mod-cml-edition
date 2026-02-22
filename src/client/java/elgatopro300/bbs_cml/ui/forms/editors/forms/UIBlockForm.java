package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.BlockForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIBlockFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIBlockForm extends UIForm<BlockForm>
{
    public UIBlockForm()
    {
        super();

        this.defaultPanel = new UIBlockFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_BLOCK_TITLE, Icons.BLOCK);
        this.registerDefaultPanels();
    }
}