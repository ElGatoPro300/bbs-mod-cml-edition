package elgatopro300.bbs_cml.forms.values;

import elgatopro300.bbs_cml.forms.forms.utils.Anchor;
import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueAnchor extends BaseKeyframeFactoryValue<Anchor>
{
    public ValueAnchor(String id, Anchor value)
    {
        super(id, KeyframeFactories.ANCHOR, value);
    }
}