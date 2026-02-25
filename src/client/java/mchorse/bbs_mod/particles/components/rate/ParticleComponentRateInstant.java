package elgatopro300.bbs_cml.particles.components.rate;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.Constant;
import elgatopro300.bbs_cml.math.Operation;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.math.molang.expressions.MolangValue;
import elgatopro300.bbs_cml.particles.components.IComponentEmitterUpdate;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentRateInstant extends ParticleComponentRate implements IComponentEmitterUpdate
{
    public static final MolangExpression DEFAULT_PARTICLES = new MolangValue(null, new Constant(10));

    public ParticleComponentRateInstant()
    {
        this.particles = DEFAULT_PARTICLES;
    }

    @Override
    protected void toData(MapType data)
    {
        if (!MolangExpression.isConstant(this.particles, 10))
        {
            data.put("num_particles", this.particles.toData());
        }
    }

    public ParticleComponentBase fromData(BaseType elem, MolangParser parser) throws MolangException
    {
        if (!elem.isMap())
        {
            return super.fromData(elem, parser);
        }

        MapType map = elem.asMap();

        if (map.has("num_particles"))
        {
            this.particles = parser.parseDataSilently(map.get("num_particles"), MolangParser.ONE);
        }

        return super.fromData(map, parser);
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        double age = emitter.getAge();

        if (emitter.playing && !emitter.paused && Operation.equals(age, 0))
        {
            emitter.setEmitterVariables(0);

            int particles = (int) this.particles.get();

            for (int i = 0, c = particles; i < c; i ++)
            {
                emitter.spawnParticle(0F);
            }
        }
    }
}