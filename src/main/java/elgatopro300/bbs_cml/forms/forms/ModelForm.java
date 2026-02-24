package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.cubic.animation.ActionsConfig;
import elgatopro300.bbs_cml.forms.values.ValueActionsConfig;
import elgatopro300.bbs_cml.forms.values.ValueShapeKeys;
import elgatopro300.bbs_cml.obj.shapes.ShapeKeys;
import elgatopro300.bbs_cml.settings.values.core.ValueColor;
import elgatopro300.bbs_cml.settings.values.core.ValueLink;
import elgatopro300.bbs_cml.settings.values.core.ValuePose;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.utils.colors.Color;
import elgatopro300.bbs_cml.utils.pose.Pose;

import java.util.ArrayList;
import java.util.List;

public class ModelForm extends Form
{
    public final ValueLink texture = new ValueLink("texture", null);
    public final ValueString model = new ValueString("model", "");
    public final ValuePose pose = new ValuePose("pose", new Pose());
    public final ValuePose poseOverlay = new ValuePose("pose_overlay", new Pose());
    public final ValueActionsConfig actions = new ValueActionsConfig("actions", new ActionsConfig());
    public final ValueColor color = new ValueColor("color", Color.white());
    public final ValueShapeKeys shapeKeys = new ValueShapeKeys("shape_keys", new ShapeKeys());

    public final List<ValuePose> additionalOverlays = new ArrayList<>();

    public ModelForm()
    {
        super();

        this.add(this.texture);
        this.add(this.model);
        this.add(this.pose);
        this.add(this.poseOverlay);

        for (int i = 0; i < BBSSettings.recordingPoseTransformOverlays.get(); i++)
        {
            ValuePose valuePose = new ValuePose("pose_overlay" + i, new Pose());

            this.additionalOverlays.add(valuePose);
            this.add(valuePose);
        }

        this.add(this.actions);
        this.add(this.color);
        this.add(this.shapeKeys);
    }

    @Override
    public String getDefaultDisplayName()
    {
        return this.model.get();
    }
}