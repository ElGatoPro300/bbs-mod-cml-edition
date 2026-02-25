package elgatopro300.bbs_cml.settings.values.core;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.colors.Color;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueColor extends BaseKeyframeFactoryValue<Color>
{
    public ValueColor(String id, Color value)
    {
        super(id, KeyframeFactories.COLOR, value);
    }
}