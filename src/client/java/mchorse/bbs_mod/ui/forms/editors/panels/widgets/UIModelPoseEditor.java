package elgatopro300.bbs_cml.ui.forms.editors.panels.widgets;

import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.settings.values.IValueListener;
import elgatopro300.bbs_cml.settings.values.core.ValuePose;
import elgatopro300.bbs_cml.ui.framework.elements.input.UIPropTransform;
import elgatopro300.bbs_cml.ui.utils.pose.UIPoseEditor;
import elgatopro300.bbs_cml.utils.pose.PoseTransform;

public class UIModelPoseEditor extends UIPoseEditor
{
    private ValuePose valuePose;

    public void setValuePose(ValuePose valuePose)
    {
        this.valuePose = valuePose;
    }

    @Override
    protected UIPropTransform createTransformEditor()
    {
        return super.createTransformEditor().callbacks(() -> this.valuePose);
    }

    @Override
    protected void pastePose(MapType data)
    {
        this.valuePose.preNotify(IValueListener.FLAG_UNMERGEABLE);
        super.pastePose(data);
        this.valuePose.postNotify(IValueListener.FLAG_UNMERGEABLE);
    }

    @Override
    protected void flipPose()
    {
        this.valuePose.preNotify(IValueListener.FLAG_UNMERGEABLE);
        super.flipPose();
        this.valuePose.postNotify(IValueListener.FLAG_UNMERGEABLE);
    }

    @Override
    protected void setFix(PoseTransform transform, float value)
    {
        this.valuePose.preNotify(IValueListener.FLAG_UNMERGEABLE);
        super.setFix(transform, value);
        this.valuePose.postNotify(IValueListener.FLAG_UNMERGEABLE);
    }

    @Override
    protected void setColor(PoseTransform transform, int value)
    {
        this.valuePose.preNotify(IValueListener.FLAG_UNMERGEABLE);
        super.setColor(transform, value);
        this.valuePose.postNotify(IValueListener.FLAG_UNMERGEABLE);
    }

    @Override
    protected void setLighting(PoseTransform transform, boolean value)
    {
        this.valuePose.preNotify(IValueListener.FLAG_UNMERGEABLE);
        super.setLighting(transform, value);
        this.valuePose.postNotify(IValueListener.FLAG_UNMERGEABLE);
    }
}