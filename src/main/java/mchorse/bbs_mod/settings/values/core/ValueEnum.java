package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.StringType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;

public class ValueEnum<E extends Enum<E>> extends BaseValueBasic<E>
{
    private Class<E> enumClass;

    public ValueEnum(String id, Class<E> enumClass, E value)
    {
        super(id, value);
        this.enumClass = enumClass;
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value.name());
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isString())
        {
            try
            {
                this.set(Enum.valueOf(this.enumClass, data.asString()));
            }
            catch (Exception e)
            {
                /* Ignore invalid values */
            }
        }
    }
}
