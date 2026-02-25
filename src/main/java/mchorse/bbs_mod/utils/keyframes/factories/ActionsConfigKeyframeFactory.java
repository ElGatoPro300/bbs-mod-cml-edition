package elgatopro300.bbs_cml.utils.keyframes.factories;

import elgatopro300.bbs_cml.cubic.animation.ActionsConfig;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.utils.interps.IInterp;

public class ActionsConfigKeyframeFactory implements IKeyframeFactory<ActionsConfig>
{
    @Override
    public ActionsConfig fromData(BaseType data)
    {
        ActionsConfig configs = new ActionsConfig();

        if (data.isMap())
        {
            configs.fromData(data.asMap());
        }

        return configs;
    }

    @Override
    public BaseType toData(ActionsConfig value)
    {
        return value.toData();
    }

    @Override
    public ActionsConfig createEmpty()
    {
        return new ActionsConfig();
    }

    @Override
    public ActionsConfig copy(ActionsConfig value)
    {
        ActionsConfig configs = new ActionsConfig();

        configs.fromData(value.toData());

        return configs;
    }

    @Override
    public ActionsConfig interpolate(ActionsConfig preA, ActionsConfig a, ActionsConfig b, ActionsConfig postB, IInterp interpolation, float x)
    {
        return a;
    }
}