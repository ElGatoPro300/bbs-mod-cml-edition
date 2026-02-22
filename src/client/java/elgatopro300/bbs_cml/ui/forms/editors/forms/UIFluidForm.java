package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.FluidForm;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIFluidFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIFluidForm extends UIForm<FluidForm>
{
    private UIFluidFormPanel fluidFormPanel;

    public UIFluidForm()
    {
        super();

        this.fluidFormPanel = new UIFluidFormPanel(this);
        this.defaultPanel = this.fluidFormPanel;

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_FLUID_TITLE, Icons.MATERIAL);
        this.registerDefaultPanels();
    }
}
