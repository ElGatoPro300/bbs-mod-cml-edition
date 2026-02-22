package elgatopro300.bbs_cml.settings.values.numeric;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueBoolean extends BaseKeyframeFactoryValue<Boolean>
{
    public ValueBoolean(String id)
    {
        this(id, false);
    }

    public ValueBoolean(String id, boolean defaultValue)
    {
        super(id, KeyframeFactories.BOOLEAN, defaultValue);
    }

    public void toggle()
    {
        this.set(!this.get());
    }

    @Override
    public String toString()
    {
        return Boolean.toString(this.value);
    }
}
