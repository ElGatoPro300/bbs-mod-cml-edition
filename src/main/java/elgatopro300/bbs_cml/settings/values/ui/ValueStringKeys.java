package elgatopro300.bbs_cml.settings.values.ui;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;

import java.util.HashSet;
import java.util.Set;

public class ValueStringKeys extends BaseValueBasic<Set<String>>
{
    public ValueStringKeys(String id)
    {
        super(id, new HashSet<>());
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (String s : this.value)
        {
            list.addString(s);
        }

        return list;
    }

    @Override
    public void fromData(BaseType data)
    {
        this.value.clear();

        if (!data.isList())
        {
            return;
        }

        for (BaseType type : data.asList())
        {
            if (type.isString()) this.value.add(type.asString());
        }
    }
}