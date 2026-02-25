package elgatopro300.bbs_cml.settings.values.misc;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;
import org.joml.Vector3f;

public class ValueVector3f extends BaseValueBasic<Vector3f>
{
    public ValueVector3f(String id)
    {
        this(id, new Vector3f());
    }

    public ValueVector3f(String id, Vector3f value)
    {
        super(id, value);
    }

    public void set(float x, float y, float z)
    {
        this.preNotify();
        this.value.set(x, y, z);
        this.postNotify();
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        list.addFloat(this.value.x);
        list.addFloat(this.value.y);
        list.addFloat(this.value.z);

        return list;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isList())
        {
            ListType list = data.asList();

            if (list.size() >= 3)
            {
                this.value.set(list.getFloat(0), list.getFloat(1), list.getFloat(2));
            }
        }
    }
    
    @Override
    public String toString()
    {
        return this.value.toString();
    }
}
