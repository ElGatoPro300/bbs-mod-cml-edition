package elgatopro300.bbs_cml.cubic.model;

import elgatopro300.bbs_cml.cubic.model.ArmorConfig;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.settings.values.core.ValueLink;
import elgatopro300.bbs_cml.settings.values.core.ValueList;
import elgatopro300.bbs_cml.settings.values.core.ValuePose;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.settings.values.misc.ValueVector3f;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.settings.values.numeric.ValueFloat;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.pose.Pose;
import org.joml.Vector3f;

public class ModelConfig extends ValueGroup
{
    public final ValueBoolean procedural = new ValueBoolean("procedural");
    public final ValueBoolean culling = new ValueBoolean("culling", true);
    public final ValueString poseGroup = new ValueString("pose_group", "");
    public final ValueString anchorGroup = new ValueString("anchor", "");
    public final ValueFloat uiScale = new ValueFloat("ui_scale", 1F);
    public final ValueVector3f scale = new ValueVector3f("scale", new Vector3f(1, 1, 1));

    public final ValuePose sneakingPose = new ValuePose("sneaking_pose", new Pose());
    public final ValuePose parts = new ValuePose("parts", new Pose());
    public final ValueInt color = new ValueInt("color", Colors.WHITE);
    public final ValueLink texture = new ValueLink("texture", null);
    public final ArmorConfig armorSlots = new ArmorConfig("armor_slots");
    public final ArmorSlot fpMain = new ArmorSlot("fp_main");
    public final ArmorSlot fpOffhand = new ArmorSlot("fp_offhand");

    public final ValueList<ValueString> itemsMain = new ValueList<ValueString>("items_main")
    {
        @Override
        protected ValueString create(String id)
        {
            return new ValueString(id, "")
            {
                @Override
                public void fromData(BaseType data)
                {
                    if (data.isMap())
                    {
                        this.value = data.asMap().getString("group");
                    }
                    else
                    {
                        super.fromData(data);
                    }
                }
            };
        }
    };
    
    public final ValueList<ValueString> itemsOff = new ValueList<ValueString>("items_off")
    {
        @Override
        protected ValueString create(String id)
        {
            return new ValueString(id, "")
            {
                @Override
                public void fromData(BaseType data)
                {
                    if (data.isMap())
                    {
                        this.value = data.asMap().getString("group");
                    }
                    else
                    {
                        super.fromData(data);
                    }
                }
            };
        }
    };

    public ModelConfig(String id)
    {
        super(id);

        this.add(this.procedural);
        this.add(this.culling);
        this.add(this.poseGroup);
        this.add(this.anchorGroup);
        this.add(this.uiScale);
        this.add(this.scale);
        this.add(this.sneakingPose);
        this.add(this.parts);
        this.add(this.color);
        this.add(this.texture);
        this.add(this.armorSlots);
        this.add(this.fpMain);
        this.add(this.fpOffhand);
        this.add(this.itemsMain);
        this.add(this.itemsOff);
    }
}
