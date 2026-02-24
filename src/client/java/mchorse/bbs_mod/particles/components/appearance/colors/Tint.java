package elgatopro300.bbs_cml.particles.components.appearance.colors;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.Constant;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.math.molang.expressions.MolangValue;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.utils.colors.Color;
import elgatopro300.bbs_cml.utils.colors.Colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Tint
{
    /**
     * Parse a single color either in hex string format or JSON array
     * (this should parse both RGB and RGBA expressions)
     */
    public static Solid parseColor(BaseType base, MolangParser parser) throws MolangException
    {
        MolangExpression r = MolangParser.ONE;
        MolangExpression g = MolangParser.ONE;
        MolangExpression b = MolangParser.ONE;
        MolangExpression a = MolangParser.ONE;

        if (base.isString())
        {
            String hex = base.asString();

            if (hex.startsWith("#") && (hex.length() == 7 || hex.length() == 9))
            {
                try
                {
                    int c = Colors.parseWithException(hex);
                    Color color = new Color().set(c, hex.length() == 9);

                    r = new MolangValue(parser, new Constant(color.r));
                    g = new MolangValue(parser, new Constant(color.g));
                    b = new MolangValue(parser, new Constant(color.b));
                    a = new MolangValue(parser, new Constant(color.a));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (base.isList())
        {
            ListType array = base.asList();
            boolean alpha = array.size() == 4;

            if (array.size() == 3 || alpha)
            {
                r = parser.parseDataSilently(array.get(0), MolangParser.ONE);
                g = parser.parseDataSilently(array.get(1), MolangParser.ONE);
                b = parser.parseDataSilently(array.get(2), MolangParser.ONE);

                if (alpha)
                {
                    a = parser.parseDataSilently(array.get(3), MolangParser.ONE);
                }
            }
        }

        return new Solid(r, g, b, a);
    }

    /**
     * Parse a gradient
     */
    public static Tint parseGradient(MapType color, MolangParser parser) throws MolangException
    {
        BaseType gradient = color.get("gradient");

        MolangExpression expression = MolangParser.ZERO;
        List<Gradient.ColorStop> colorStops = new ArrayList<>();
        boolean equal = true;

        if (gradient.isMap())
        {
            for (Map.Entry<String, BaseType> entry : gradient.asMap())
            {
                Solid stopColor = parseColor(entry.getValue(), parser);

                colorStops.add(new Gradient.ColorStop(Float.parseFloat(entry.getKey()), stopColor));
            }

            Collections.sort(colorStops, (a, b) -> a.stop > b.stop ? 1 : -1);
            equal = false;
        }
        else if (gradient.isList())
        {
            ListType colors = gradient.asList();

            int i = 0;

            for (BaseType stop : colors)
            {
                colorStops.add(new Gradient.ColorStop(i / (float) (colors.size() - 1), parseColor(stop, parser)));

                i ++;
            }
        }

        if (color.has("interpolant"))
        {
            expression = parser.parseDataSilently(color.get("interpolant"));
        }

        return new Gradient(colorStops, expression, equal);
    }

    public abstract void compute(Particle particle);

    public abstract BaseType toData();

}