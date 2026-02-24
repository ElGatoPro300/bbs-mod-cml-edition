package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.LabelForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UILabelFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UILabelForm extends UIForm<LabelForm>
{
    public UILabelForm()
    {
        super();

        this.defaultPanel = new UILabelFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_LABEL_TITLE, Icons.FONT);
        this.registerDefaultPanels();
    }
}