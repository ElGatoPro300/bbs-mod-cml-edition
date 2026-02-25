package elgatopro300.bbs_cml.utils.keyframes.factories;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ByteType;
import elgatopro300.bbs_cml.utils.interps.IInterp;

public class BooleanKeyframeFactory implements IKeyframeFactory<Boolean>
{
    @Override
    public Boolean fromData(BaseType data)
    {
        return data.isNumeric() && data.asNumeric().boolValue();
    }

    @Override
    public BaseType toData(Boolean value)
    {
        return new ByteType(value);
    }

    @Override
    public Boolean createEmpty()
    {
        return false;
    }

    @Override
    public Boolean copy(Boolean value)
    {
        return value;
    }

    @Override
    public Boolean interpolate(Boolean preA, Boolean a, Boolean b, Boolean postB, IInterp interpolation, float x)
    {
        return a;
    }
}