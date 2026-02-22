package elgatopro300.bbs_cml.camera.values;

import elgatopro300.bbs_cml.camera.data.Angle;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;

public class ValueAngle extends BaseValueBasic<Angle>
{
    public ValueAngle(String id, Angle angle)
    {
        super(id, angle);
    }

    @Override
    public void set(Angle value, int flag)
    {
        this.preNotify();
        this.value.set(value);
        this.postNotify();
    }

    @Override
    public BaseType toData()
    {
        return this.value.toData();
    }

    @Override
    public void fromData(BaseType data)
    {
        this.value.fromData(data.asMap());
    }
}