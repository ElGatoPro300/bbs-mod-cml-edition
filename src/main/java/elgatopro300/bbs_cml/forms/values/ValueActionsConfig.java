package elgatopro300.bbs_cml.forms.values;

import elgatopro300.bbs_cml.cubic.animation.ActionsConfig;
import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueActionsConfig extends BaseKeyframeFactoryValue<ActionsConfig>
{
    public ValueActionsConfig(String id, ActionsConfig value)
    {
        super(id, KeyframeFactories.ACTIONS_CONFIG, value);
    }
}