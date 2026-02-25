package elgatopro300.bbs_cml.math.functions.string;

import elgatopro300.bbs_cml.math.IExpression;
import elgatopro300.bbs_cml.math.MathBuilder;
import elgatopro300.bbs_cml.math.functions.SNFunction;

public class StringStartsWith extends SNFunction
{
    public StringStartsWith(MathBuilder builder, IExpression[] expressions, String name) throws Exception
    {
        super(builder, expressions, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double doubleValue()
    {
        return this.getArg(0).stringValue().startsWith(this.getArg(1).stringValue()) ? 1 : 0;
    }
}
