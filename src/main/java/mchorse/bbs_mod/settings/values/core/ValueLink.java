package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueLink extends BaseKeyframeFactoryValue<Link>
{
    public ValueLink(String id, Link defaultValue)
    {
        super(id, KeyframeFactories.LINK, defaultValue);
    }

    @Override
    public String toString()
    {
        return this.value == null ? "" : this.value.toString();
    }
}