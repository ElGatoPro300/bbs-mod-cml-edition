package elgatopro300.bbs_cml.math.functions.classic;

import elgatopro300.bbs_cml.math.IExpression;
import elgatopro300.bbs_cml.math.MathBuilder;
import elgatopro300.bbs_cml.math.functions.NNFunction;

/**
 * Absolute value function 
 */
public class Abs extends NNFunction
{
    public Abs(MathBuilder builder, IExpression[] expressions, String name) throws Exception
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
        return Math.abs(this.getArg(0).doubleValue());
    }
}