package elgatopro300.bbs_cml.settings.values.misc;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import org.joml.Vector4f;

public class ValueVector4f extends BaseKeyframeFactoryValue<Vector4f>
{
    public ValueVector4f(String id, Vector4f value)
    {
        super(id, KeyframeFactories.VECTOR4F, value);
    }
}