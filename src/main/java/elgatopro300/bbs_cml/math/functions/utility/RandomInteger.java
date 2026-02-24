package elgatopro300.bbs_cml.math.functions.utility;

import elgatopro300.bbs_cml.math.IExpression;
import elgatopro300.bbs_cml.math.MathBuilder;

public class RandomInteger extends Random
{
    public RandomInteger(MathBuilder builder, IExpression[] expressions, String name) throws Exception
    {
        super(builder, expressions, name);
    }

    @Override
    public double doubleValue()
    {
        return (int) super.doubleValue();
    }
}