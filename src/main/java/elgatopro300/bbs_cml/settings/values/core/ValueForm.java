package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;

public class ValueForm extends BaseValueBasic<Form>
{
    public ValueForm(String id)
    {
        super(id, null);
    }

    @Override
    public BaseType toData()
    {
        return this.value == null ? null : FormUtils.toData(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data != null && data.isMap())
        {
            this.value = FormUtils.fromData(data.asMap());
        }
        else
        {
            this.value = null;
        }
    }
}