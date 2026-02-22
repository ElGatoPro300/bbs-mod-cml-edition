package elgatopro300.bbs_cml.particles;

import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;

public class ParticleUtils
{
    public static ListType vectorToList(MolangExpression[] expressions)
    {
        ListType list = new ListType();

        for (MolangExpression expression : expressions)
        {
            list.add(expression.toData());
        }

        return list;
    }

    public static void vectorFromList(ListType list, MolangExpression[] expressions, MolangParser parser) throws MolangException
    {
        if (list.size() >= expressions.length)
        {
            for (int i = 0; i < expressions.length; i++)
            {
                expressions[i] = parser.parseDataSilently(list.get(i));
            }
        }
    }
}