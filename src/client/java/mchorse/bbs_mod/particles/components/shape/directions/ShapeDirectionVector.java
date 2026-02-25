package elgatopro300.bbs_cml.particles.components.shape.directions;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.emitter.Particle;

public class ShapeDirectionVector extends ShapeDirection
{
    public MolangExpression x;
    public MolangExpression y;
    public MolangExpression z;

    public ShapeDirectionVector(MolangExpression x, MolangExpression y, MolangExpression z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void applyDirection(Particle particle, double x, double y, double z)
    {
        particle.speed.set((float) this.x.get(), (float) this.y.get(), (float) this.z.get());

        if (particle.speed.length() <= 0)
        {
            particle.speed.set(0, 0, 0);
        }
        else
        {
            particle.speed.normalize();
        }
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        list.add(this.x.toData());
        list.add(this.y.toData());
        list.add(this.z.toData());

        return list;
    }
}