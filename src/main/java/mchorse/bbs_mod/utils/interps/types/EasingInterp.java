package elgatopro300.bbs_cml.utils.interps.types;

import elgatopro300.bbs_cml.utils.interps.InterpContext;
import elgatopro300.bbs_cml.utils.interps.Lerps;
import elgatopro300.bbs_cml.utils.interps.easings.IEasing;

public class EasingInterp extends BaseInterp
{
    public final IEasing easing;

    public EasingInterp(String key, int keybind, IEasing easing)
    {
        super(key, keybind);

        this.easing = easing;
    }

    @Override
    public double interpolate(InterpContext context)
    {
        return Lerps.lerp(context.a, context.b, this.easing.calculate(context.getArgs(), context.x));
    }
}