package elgatopro300.bbs_cml.utils.keyframes.factories;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.utils.interps.IInterp;
import elgatopro300.bbs_cml.utils.pose.Transform;

public class TransformKeyframeFactory implements IKeyframeFactory<Transform>
{
    private Transform i = new Transform();

    @Override
    public Transform fromData(BaseType data)
    {
        Transform transform = new Transform();

        if (data.isMap())
        {
            transform.fromData(data.asMap());
        }

        return transform;
    }

    @Override
    public BaseType toData(Transform value)
    {
        return value.toData();
    }

    @Override
    public Transform createEmpty()
    {
        return new Transform();
    }

    @Override
    public Transform copy(Transform value)
    {
        return value.copy();
    }

    @Override
    public Transform interpolate(Transform preA, Transform a, Transform b, Transform postB, IInterp interpolation, float x)
    {
        this.i.lerp(preA, a, b, postB, interpolation, x);

        return this.i;
    }
}