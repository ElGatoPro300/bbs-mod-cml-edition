package elgatopro300.bbs_cml.settings.values.numeric;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.FloatType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueNumber;
import elgatopro300.bbs_cml.utils.MathUtils;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueFloat extends BaseValueNumber<Float>
{
    public ValueFloat(String id, Float defaultValue)
    {
        this(id, defaultValue, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, Float defaultValue, Float min, Float max)
    {
        super(id, KeyframeFactories.FLOAT, defaultValue, min, max);
    }

    @Override
    protected Float clamp(Float value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

    @Override
    public BaseType toData()
    {
        return new FloatType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.value = data.asNumeric().floatValue();
        }
    }

    @Override
    public String toString()
    {
        return Float.toString(this.value);
    }
}