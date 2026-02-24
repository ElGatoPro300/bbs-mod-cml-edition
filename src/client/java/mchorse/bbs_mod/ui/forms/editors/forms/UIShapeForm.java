package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.ShapeForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIShapeFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIShapeForm extends UIForm<ShapeForm>
{
    private UIShapeFormPanel shapeFormPanel;

    public UIShapeForm()
    {
        super();

        this.shapeFormPanel = new UIShapeFormPanel(this);
        this.defaultPanel = this.shapeFormPanel;

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_BILLBOARD_TITLE, Icons.GEAR);
        this.registerDefaultPanels();
    }
}
