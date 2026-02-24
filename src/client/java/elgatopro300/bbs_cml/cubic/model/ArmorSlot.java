package elgatopro300.bbs_cml.cubic.model;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.utils.pose.Transform;

public class ArmorSlot extends ValueGroup
{
    public final ValueString group = new ValueString("group", "");
    public final Transform transform = new Transform();

    public ArmorSlot(String id)
    {
        super(id);

        this.add(this.group);
    }

    @Override
    public BaseType toData()
    {
        MapType data = (MapType) super.toData();
        Transform transform = this.transform.copy();

        transform.toDeg();
        data.put("transform", transform.toData());

        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isString())
        {
            this.group.set(data.asString());
            return;
        }

        super.fromData(data);

        if (data.isMap())
        {
            MapType map = data.asMap();

            if (map.has("transform"))
            {
                this.transform.fromData(map.getMap("transform"));
                this.transform.toRad();
            }
        }
    }

    public ArmorSlot copy()
    {
        ArmorSlot slot = new ArmorSlot(this.getId());

        slot.group.set(this.group.get());
        slot.transform.copy(this.transform);

        return slot;
    }
}
