package elgatopro300.bbs_cml.particles.components.expiration;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.components.IComponentParticleInitialize;
import elgatopro300.bbs_cml.particles.components.IComponentParticleUpdate;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentParticleLifetime extends ParticleComponentBase implements IComponentParticleInitialize, IComponentParticleUpdate
{
    public MolangExpression expression = MolangParser.ZERO;
    public boolean max;

    @Override
    protected void toData(MapType data)
    {
        data.put(this.max ? "max_lifetime" : "expiration_expression", this.expression.toData());
    }

    @Override
    public ParticleComponentBase fromData(BaseType elem, MolangParser parser) throws MolangException
    {
        if (!elem.isMap())
        {
            return super.fromData(elem, parser);
        }

        MapType element = elem.asMap();
        BaseType expression = null;

        if (element.has("expiration_expression"))
        {
            expression = element.get("expiration_expression");
            this.max = false;
        }
        else if (element.has("max_lifetime"))
        {
            expression = element.get("max_lifetime");
            this.max = true;
        }
        else
        {
            throw new RuntimeException("No expiration_expression or max_lifetime was found in particle_lifetime_expression component");
        }

        this.expression = parser.parseDataSilently(expression, MolangParser.ONE);

        return super.fromData(element, parser);
    }

    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        if (!this.max && this.expression.get() != 0)
        {
            particle.setDead();
        }
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        if (this.max)
        {
            particle.lifetime = (int) (this.expression.get() * 20);
        }
        else
        {
            particle.lifetime = -1;
        }
    }
}