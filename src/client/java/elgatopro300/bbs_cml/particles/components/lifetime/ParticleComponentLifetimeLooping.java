package elgatopro300.bbs_cml.particles.components.lifetime;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentLifetimeLooping extends ParticleComponentLifetime
{
    public MolangExpression sleepTime = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        super.toData(data);

        if (!MolangExpression.isZero(this.sleepTime))
        {
            data.put("sleep_time", this.sleepTime.toData());
        }
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType element = data.asMap();

        if (element.has("sleep_time"))
        {
            this.sleepTime = parser.parseDataSilently(element.get("sleep_time"));
        }

        return super.fromData(element, parser);
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        double active = this.activeTime.get();
        double sleep = this.sleepTime.get();
        double age = emitter.getAge();

        emitter.lifetime = (int) (active * 20);

        if (age >= active && emitter.playing)
        {
            emitter.stop();
        }

        if (age >= sleep && !emitter.playing)
        {
            emitter.start();
        }
    }
}