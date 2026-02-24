package elgatopro300.bbs_cml.particles.components.lifetime;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.Operation;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentLifetimeExpression extends ParticleComponentLifetime
{
    public MolangExpression expiration = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        super.toData(data);

        if (!MolangExpression.isZero(this.expiration))
        {
            data.put("expiration_expression", this.expiration.toData());
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

        if (map.has("expiration_expression"))
        {
            this.expiration = parser.parseDataSilently(map.get("expiration_expression"));
        }

        return super.fromData(map, parser);
    }

    @Override
    protected String getPropertyName()
    {
        return "activation_expression";
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        if (!Operation.equals(this.activeTime.get(), 0))
        {
            emitter.start();
        }

        if (!Operation.equals(this.expiration.get(), 0))
        {
            emitter.stop();
        }
    }
}