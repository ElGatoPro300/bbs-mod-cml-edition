package elgatopro300.bbs_cml.particles.components.lifetime;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.Constant;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.math.molang.expressions.MolangValue;
import elgatopro300.bbs_cml.particles.components.IComponentEmitterUpdate;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;

public abstract class ParticleComponentLifetime extends ParticleComponentBase implements IComponentEmitterUpdate
{
    public static final MolangExpression DEFAULT_ACTIVE = new MolangValue(null, new Constant(10));

    public MolangExpression activeTime = DEFAULT_ACTIVE;

    @Override
    protected void toData(MapType data)
    {
        if (!MolangExpression.isConstant(this.activeTime, 10))
        {
            data.put(this.getPropertyName(), this.activeTime.toData());
        }
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has(this.getPropertyName()))
        {
            this.activeTime = parser.parseDataSilently(map.get(this.getPropertyName()), MolangParser.ONE);
        }

        return super.fromData(map, parser);
    }

    protected String getPropertyName()
    {
        return "active_time";
    }

    @Override
    public int getSortingIndex()
    {
        return -10;
    }
}