package elgatopro300.bbs_cml.math.functions.limit;

import elgatopro300.bbs_cml.math.IExpression;
import elgatopro300.bbs_cml.math.MathBuilder;
import elgatopro300.bbs_cml.math.functions.NNFunction;
import elgatopro300.bbs_cml.utils.MathUtils;

public class Clamp extends NNFunction
{
    public Clamp(MathBuilder builder, IExpression[] expressions, String name) throws Exception
    {
        super(builder, expressions, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double doubleValue()
    {
        return MathUtils.clamp(this.getArg(0).doubleValue(), this.getArg(1).doubleValue(), this.getArg(2).doubleValue());
    }
}