package elgatopro300.bbs_cml.particles.components.motion;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.components.IComponentParticleInitialize;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentInitialSpin extends ParticleComponentBase implements IComponentParticleInitialize
{
    public MolangExpression rotation = MolangParser.ZERO;
    public MolangExpression rate = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        if (!MolangExpression.isZero(this.rotation)) data.put("rotation", this.rotation.toData());
        if (!MolangExpression.isZero(this.rate)) data.put("rotation_rate", this.rate.toData());
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("rotation")) this.rotation = parser.parseDataSilently(map.get("rotation"));
        if (map.has("rotation_rate")) this.rate = parser.parseDataSilently(map.get("rotation_rate"));

        return super.fromData(map, parser);
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        particle.initialRotation = (float) this.rotation.get();
        particle.rotationVelocity = (float) this.rate.get() / 20;
    }
}