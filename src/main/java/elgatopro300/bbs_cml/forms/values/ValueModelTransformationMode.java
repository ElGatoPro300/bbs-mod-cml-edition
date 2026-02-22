package elgatopro300.bbs_cml.forms.values;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.StringType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;
import net.minecraft.item.ModelTransformationMode;

public class ValueModelTransformationMode extends BaseValueBasic<ModelTransformationMode>
{
    public ValueModelTransformationMode(String id, ModelTransformationMode value)
    {
        super(id, value);
    }

    @Override
    public BaseType toData()
    {
        return new StringType((this.value == null ? ModelTransformationMode.NONE : this.value).asString());
    }

    @Override
    public void fromData(BaseType data)
    {
        String string = data.isString() ? data.asString() : "";

        this.set(ModelTransformationMode.NONE);

        for (ModelTransformationMode value : ModelTransformationMode.values())
        {
            if (value.asString().equals(string))
            {
                this.set(value);

                break;
            }
        }
    }
}