package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.forms.TrailForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UITrailFormPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UITrailForm extends UIForm<TrailForm>
{
    public UITrailFormPanel trailFormPanel;

    public UITrailForm()
    {
        super();

        this.trailFormPanel = new UITrailFormPanel(this);
        this.defaultPanel = this.trailFormPanel;

        this.registerPanel(this.trailFormPanel, UIKeys.FORMS_EDITORS_TRAIL_TITLE, Icons.PLAY);
        this.registerDefaultPanels();
    }
}