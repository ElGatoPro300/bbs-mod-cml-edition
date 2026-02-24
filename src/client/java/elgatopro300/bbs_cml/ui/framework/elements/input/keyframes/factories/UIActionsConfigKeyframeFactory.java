package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.cubic.animation.ActionsConfig;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.renderers.ModelFormRenderer;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.pose.UIActionsConfigEditor;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

public class UIActionsConfigKeyframeFactory extends UIKeyframeFactory<ActionsConfig>
{
    public UIActionsConfigEditor actionsEditor;

    public UIActionsConfigKeyframeFactory(Keyframe<ActionsConfig> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        ModelForm form = (ModelForm) FormUtils.getForm(editor.getGraph().getSheet(keyframe).property);
        ModelFormRenderer renderer = (ModelFormRenderer) FormUtilsClient.getRenderer(form);

        this.actionsEditor = new UIActionsConfigEditor(() ->
        {
            this.keyframe.preNotify();
        }, () ->
        {
            renderer.resetAnimator();
            this.keyframe.postNotify();
        });
        this.actionsEditor.setConfigs(keyframe.getValue(), form);

        this.scroll.add(this.actionsEditor);
    }

    @Override
    public void resize()
    {
        this.actionsEditor.removeAll();

        if (this.getFlex().getW() > 240)
        {
            this.actionsEditor.add(UI.row(
                UI.column(
                    UI.label(UIKeys.FORMS_EDITORS_MODEL_ACTIONS), this.actionsEditor.actionsSearch,
                    UI.label(UIKeys.FORMS_EDITORS_ACTIONS_SPEED).marginTop(6), this.actionsEditor.speed,
                    this.actionsEditor.loop.marginTop(20)
                ),
                UI.column(
                    UI.label(UIKeys.FORMS_EDITORS_ACTIONS_ANIMATIONS), this.actionsEditor.animations,
                    UI.label(UIKeys.FORMS_EDITORS_ACTIONS_FADE).marginTop(6), this.actionsEditor.fade,
                    UI.label(UIKeys.FORMS_EDITORS_ACTIONS_TICK).marginTop(6), this.actionsEditor.tick
                )
            ));
        }
        else
        {
            this.actionsEditor.add(UI.label(UIKeys.FORMS_EDITORS_MODEL_ACTIONS), this.actionsEditor.actionsSearch);
            this.actionsEditor.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_ANIMATIONS).marginTop(6), this.actionsEditor.animations, this.actionsEditor.loop.marginTop(6));
            this.actionsEditor.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_SPEED).marginTop(6), this.actionsEditor.speed);
            this.actionsEditor.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_FADE).marginTop(6), this.actionsEditor.fade);
            this.actionsEditor.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_TICK).marginTop(6), this.actionsEditor.tick);
        }

        super.resize();
    }
}