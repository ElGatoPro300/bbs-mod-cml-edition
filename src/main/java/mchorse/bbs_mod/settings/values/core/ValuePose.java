package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import elgatopro300.bbs_cml.utils.pose.Pose;

public class ValuePose extends BaseKeyframeFactoryValue<Pose>
{
    public ValuePose(String id, Pose value)
    {
        super(id, KeyframeFactories.POSE, value);
    }
}