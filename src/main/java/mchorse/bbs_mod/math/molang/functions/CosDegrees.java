package elgatopro300.bbs_cml.math.molang.functions;

import elgatopro300.bbs_cml.math.IExpression;
import elgatopro300.bbs_cml.math.MathBuilder;
import elgatopro300.bbs_cml.math.functions.NNFunction;

public class CosDegrees extends NNFunction
{
    public CosDegrees(MathBuilder builder, IExpression[] expressions, String name) throws Exception
    {
        super(builder, expressions, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        return Math.cos(this.getArg(0).doubleValue() / 180 * Math.PI);
    }
}