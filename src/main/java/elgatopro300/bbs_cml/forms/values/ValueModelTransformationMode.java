package elgatopro300.bbs_cml.forms.values;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.StringType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;
import elgatopro300.bbs_cml.items.ItemDisplayMode;

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