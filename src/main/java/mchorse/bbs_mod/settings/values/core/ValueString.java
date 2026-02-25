package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueString extends BaseKeyframeFactoryValue<String>
{
    public ValueString(String id, String defaultValue)
    {
        super(id, KeyframeFactories.STRING, defaultValue);
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}