package elgatopro300.bbs_cml.settings.values.ui;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.StringType;
import elgatopro300.bbs_cml.settings.values.core.ValueString;

/**
 * Value language.
 *
 * <p>This value subclass stores language localization ID. IMPORTANT: the
 * language strings don't get reloaded automatically! You need to attach a
 * callback to the value.</p>
 */
public class ValueLanguage extends ValueString
{
    public ValueLanguage(String id)
    {
        super(id, "");
    }

    @Override
    public void fromData(BaseType data)
    {
        if (BaseType.isString(data))
        {
            data = new StringType(data.asString().toLowerCase());
        }

        super.fromData(data);
    }
}