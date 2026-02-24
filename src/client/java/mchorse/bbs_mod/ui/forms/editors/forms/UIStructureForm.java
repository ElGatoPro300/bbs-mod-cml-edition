package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.StructureForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIStructureFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIStructureForm extends UIForm<StructureForm>
{
    public UIStructureForm()
    {
        super();

        this.defaultPanel = new UIStructureFormPanel(this);

        /* Usar el icono de árbol para estructuras */
        this.registerPanel(this.defaultPanel, IKey.raw("Structure"), Icons.TREE);
        this.registerDefaultPanels();
    }
}