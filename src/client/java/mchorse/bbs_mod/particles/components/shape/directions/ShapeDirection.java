package elgatopro300.bbs_cml.particles.components.shape.directions;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.particles.emitter.Particle;

public abstract class ShapeDirection
{
    public abstract void applyDirection(Particle particle, double x, double y, double z);

    public abstract BaseType toData();
}