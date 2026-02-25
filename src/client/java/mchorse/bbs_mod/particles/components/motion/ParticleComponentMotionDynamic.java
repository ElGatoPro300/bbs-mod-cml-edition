package elgatopro300.bbs_cml.particles.components.motion;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.ParticleUtils;
import elgatopro300.bbs_cml.particles.components.IComponentParticleUpdate;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentMotionDynamic extends ParticleComponentMotion implements IComponentParticleUpdate
{
    public MolangExpression[] motionAcceleration = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};
    public MolangExpression motionDrag = MolangParser.ZERO;
    public MolangExpression rotationAcceleration = MolangParser.ZERO;
    public MolangExpression rotationDrag = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        data.put("linear_acceleration", ParticleUtils.vectorToList(this.motionAcceleration));

        if (!MolangExpression.isZero(this.motionDrag)) data.put("linear_drag_coefficient", this.motionDrag.toData());
        if (!MolangExpression.isZero(this.rotationAcceleration)) data.put("rotation_acceleration", this.rotationAcceleration.toData());
        if (!MolangExpression.isZero(this.rotationDrag)) data.put("rotation_drag_coefficient", this.rotationDrag.toData());
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("linear_acceleration"))
        {
            ParticleUtils.vectorFromList(map.getList("linear_acceleration"), this.motionAcceleration, parser);
        }

        if (map.has("linear_drag_coefficient")) this.motionDrag = parser.parseDataSilently(map.get("linear_drag_coefficient"));
        if (map.has("rotation_acceleration")) this.rotationAcceleration = parser.parseDataSilently(map.get("rotation_acceleration"));
        if (map.has("rotation_drag_coefficient")) this.rotationDrag = parser.parseDataSilently(map.get("rotation_drag_coefficient"));

        return super.fromData(map, parser);
    }

    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        particle.acceleration.x += (float) this.motionAcceleration[0].get();
        particle.acceleration.y += (float) this.motionAcceleration[1].get();
        particle.acceleration.z += (float) this.motionAcceleration[2].get();
        particle.drag = (float) this.motionDrag.get();

        particle.rotationAcceleration += (float) this.rotationAcceleration.get() / 20F;
        particle.rotationDrag = (float) this.rotationDrag.get();
    }
}