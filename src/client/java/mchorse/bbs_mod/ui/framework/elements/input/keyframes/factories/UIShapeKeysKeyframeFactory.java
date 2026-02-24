package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.cubic.ModelInstance;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.renderers.ModelFormRenderer;
import elgatopro300.bbs_cml.obj.shapes.ShapeKeys;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.ui.utils.shapes.UIShapeKeys;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

import java.util.Set;

public class UIShapeKeysKeyframeFactory extends UIKeyframeFactory<ShapeKeys>
{
    private UIShapeKeys shapeKeys;

    public UIShapeKeysKeyframeFactory(Keyframe<ShapeKeys> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        UIKeyframeSheet sheet = editor.getGraph().getSheet(keyframe);
        ModelForm form = (ModelForm) FormUtils.getForm(sheet.property);
        ModelInstance model = ((ModelFormRenderer) FormUtilsClient.getRenderer(form)).getModel();
        Set<String> shapeKeys = model.model.getShapeKeys();

        this.shapeKeys = new UIShapeKeysEditor(this);

        if (!shapeKeys.isEmpty())
        {
            this.shapeKeys.setShapeKeys(model.poseGroup, shapeKeys, keyframe.getValue());
            this.scroll.add(this.shapeKeys);
        }
    }

    public static class UIShapeKeysEditor extends UIShapeKeys
    {
        private UIShapeKeysKeyframeFactory editor;

        public UIShapeKeysEditor(UIShapeKeysKeyframeFactory editor)
        {
            this.editor = editor;
        }

        @Override
        protected void changedShapeKeys(Runnable runnable)
        {
            super.changedShapeKeys(runnable);
        }

        @Override
        protected void setValue(float v)
        {
            this.editor.keyframe.preNotify();
            super.setValue(v);
            this.editor.keyframe.postNotify();
        }
    }
}