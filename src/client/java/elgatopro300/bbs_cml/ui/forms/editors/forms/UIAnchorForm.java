package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.AnchorForm;

public class UIAnchorForm extends UIForm<AnchorForm>
{
    public UIAnchorForm()
    {
        super();

        this.registerDefaultPanels();

        this.defaultPanel = this.panels.get(0);
    }
}