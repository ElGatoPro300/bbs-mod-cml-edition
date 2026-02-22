package elgatopro300.bbs_cml.forms.values;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.StringType;
import mchorse.bbs_mod.settings.values.base.BaseValueBasic;
import mchorse.bbs_mod.items.ItemDisplayMode;

public class ValueModelTransformationMode extends BaseValueBasic<ItemDisplayMode>
{
    public ValueModelTransformationMode(String id, ItemDisplayMode value)
    {
        super(id, value);
    }

    @Override
    public BaseType toData()
    {
        return new StringType((this.value == null ? ItemDisplayMode.NONE : this.value).asString());
    }

    @Override
    public void fromData(BaseType data)
    {
        String string = data.isString() ? data.asString() : "";

        this.set(ItemDisplayMode.NONE);

        for (ItemDisplayMode value : ItemDisplayMode.values())
        {
            if (value.asString().equals(string))
            {
                this.set(value);

                break;
            }
        }
    }
}