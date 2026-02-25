package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.settings.values.core.ValuePose;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.settings.values.core.ValueLink;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.utils.pose.Pose;

public class MobForm extends Form
{
    public final ValueString mobID = new ValueString("mobId", "minecraft:chicken");
    public final ValueString mobNBT = new ValueString("mobNbt", "");

    public final ValueLink texture = new ValueLink("texture", null);
    public final ValueBoolean slim = new ValueBoolean("slim", false);

    public final ValuePose pose = new ValuePose("pose", new Pose());
    public final ValuePose poseOverlay = new ValuePose("pose_overlay", new Pose());

    public MobForm()
    {
        this.slim.invisible();

        this.add(this.mobID);
        this.add(this.mobNBT);
        this.add(this.pose);
        this.add(this.poseOverlay);
        this.add(this.texture);
        this.add(this.slim);
    }

    @Override
    protected String getDefaultDisplayName()
    {
        return this.mobID.get().isEmpty() ? super.getDefaultDisplayName() : this.mobID.get();
    }

    public boolean isPlayer()
    {
        return this.mobID.get().equals("minecraft:player");
    }
}