package elgatopro300.bbs_cml.particles.components.shape;

import elgatopro300.bbs_cml.particles.components.shape.directions.ShapeDirectionVector;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentShapePoint extends ParticleComponentShapeBase
{
    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        particle.position.x = (float) this.offset[0].get();
        particle.position.y = (float) this.offset[1].get();
        particle.position.z = (float) this.offset[2].get();

        if (this.direction instanceof ShapeDirectionVector)
        {
            this.direction.applyDirection(particle, particle.position.x, particle.position.y, particle.position.z);
        }
    }
}