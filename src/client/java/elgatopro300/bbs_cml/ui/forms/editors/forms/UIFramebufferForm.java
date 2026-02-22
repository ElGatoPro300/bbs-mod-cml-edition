package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.FramebufferForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIFramebufferFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIFramebufferForm extends UIForm<FramebufferForm>
{
    public UIFramebufferForm()
    {
        super();

        this.defaultPanel = new UIFramebufferFormPanel(this);

        this.registerPanel(this.defaultPanel, IKey.raw("Framebuffer options"), Icons.CAMERA);
        this.registerDefaultPanels();
    }
}