package elgatopro300.bbs_cml.camera.values;

import elgatopro300.bbs_cml.camera.data.Point;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;

public class ValuePoint extends BaseValueBasic<Point>
{
    public ValuePoint(String id, Point point)
    {
        super(id, point);
    }

    @Override
    public void set(Point value, int flag)
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