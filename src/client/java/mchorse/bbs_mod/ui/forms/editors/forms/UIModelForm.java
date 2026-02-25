package elgatopro300.bbs_cml.ui.forms.editors.forms;

import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIActionsFormPanel;
import elgatopro300.bbs_cml.ui.forms.editors.panels.UIModelFormPanel;
import elgatopro300.bbs_cml.ui.framework.elements.input.UIPropTransform;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.ui.utils.pose.UIPoseEditor;
import elgatopro300.bbs_cml.utils.StringUtils;
import org.joml.Matrix4f;

public class UIModelForm extends UIForm<ModelForm>
{
    public UIModelFormPanel modelPanel;

    public UIModelForm()
    {
        this.modelPanel = new UIModelFormPanel(this);
        this.defaultPanel = this.modelPanel;

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_MODEL_POSE, Icons.POSE);
        this.registerPanel(new UIActionsFormPanel(this), UIKeys.FORMS_EDITORS_ACTIONS_TITLE, Icons.MORE);
        this.registerDefaultPanels();

        this.defaultPanel.keys().register(Keys.FORMS_PICK_TEXTURE, () ->
        {
            if (this.view != this.modelPanel)
            {
                this.setPanel(this.modelPanel);
            }

            this.modelPanel.pick.clickItself();
        });
    }

    @Override
    public UIPropTransform getEditableTransform()
    {
        return this.modelPanel.poseEditor.transform;
    }

    @Override
    public Matrix4f getOrigin(float transition)
    {
        String path = FormUtils.getPath(this.form);
        UIPoseEditor poseEditor = this.modelPanel.poseEditor;

        return this.getOrigin(transition, StringUtils.combinePaths(path, poseEditor.groups.list.getCurrentFirst()), poseEditor.transform.isLocal());
    }
}