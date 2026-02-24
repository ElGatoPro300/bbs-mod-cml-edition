package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import elgatopro300.bbs_cml.utils.pose.Transform;

public class ValueTransform extends BaseKeyframeFactoryValue<Transform>
{
    public ValueTransform(String id, Transform transform)
    {
        super(id, KeyframeFactories.TRANSFORM, transform);
    }
}