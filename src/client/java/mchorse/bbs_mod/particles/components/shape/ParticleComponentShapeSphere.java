package elgatopro300.bbs_cml.particles.components.shape;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;
import org.joml.Vector3f;

public class ParticleComponentShapeSphere extends ParticleComponentShapeBase
{
    public MolangExpression radius = MolangParser.ZERO;

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (!MolangExpression.isZero(this.radius))
        {
            data.put("radius", this.radius.toData());
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

        if (map.has("radius"))
        {
            this.radius = parser.parseDataSilently(map.get("radius"), MolangParser.ONE);
        }

        return super.fromData(map, parser);
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        float centerX = (float) this.offset[0].get();
        float centerY = (float) this.offset[1].get();
        float centerZ = (float) this.offset[2].get();
        float radius = (float) this.radius.get();

        Vector3f direction = new Vector3f((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
        direction.normalize();

        if (!this.surface)
        {
            radius *= Math.random();
        }

        direction.mul(radius);

        particle.position.x = centerX + direction.x;
        particle.position.y = centerY + direction.y;
        particle.position.z = centerZ + direction.z;

        this.direction.applyDirection(particle, centerX, centerY, centerZ);
    }
}