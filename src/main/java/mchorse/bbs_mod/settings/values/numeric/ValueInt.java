package elgatopro300.bbs_cml.settings.values.numeric;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.settings.values.base.BaseValueNumber;
import elgatopro300.bbs_cml.utils.MathUtils;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValueInt extends BaseValueNumber<Integer>
{
    private Subtype subtype = Subtype.INTEGER;
    private List<IKey> labels;

    public ValueInt(String id, Integer defaultValue)
    {
        this(id, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public ValueInt(String id, Integer defaultValue, Integer min, Integer max)
    {
        super(id, KeyframeFactories.INTEGER, defaultValue, min, max);
    }

    @Override
    protected Integer clamp(Integer value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

    public Subtype getSubtype()
    {
        return this.subtype;
    }

    public List<IKey> getLabels()
    {
        return this.labels;
    }

    public ValueInt subtype(Subtype subtype)
    {
        this.subtype = subtype;

        return this;
    }

    public ValueInt color()
    {
        return this.subtype(Subtype.COLOR);
    }

    public ValueInt colorAlpha()
    {
        return this.subtype(Subtype.COLOR_ALPHA);
    }

    public ValueInt modes(IKey... labels)
    {
        this.labels = new ArrayList<>();
        Collections.addAll(this.labels, labels);

        return this.subtype(Subtype.MODES);
    }

    @Override
    public String toString()
    {
        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            return "#" + Integer.toHexString(this.value);
        }

        return Integer.toString(this.value);
    }

    public static enum Subtype
    {
        INTEGER,
        COLOR,
        COLOR_ALPHA,
        MODES
    }
}