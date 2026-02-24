package elgatopro300.bbs_cml.particles.functions;

import elgatopro300.bbs_cml.math.IExpression;
import elgatopro300.bbs_cml.math.MathBuilder;
import elgatopro300.bbs_cml.math.functions.SNFunction;
import elgatopro300.bbs_cml.particles.ParticleMolangParser;
import elgatopro300.bbs_cml.particles.emitter.Particle;

public class GetParticleVariable extends SNFunction
{
    public GetParticleVariable(MathBuilder builder, IExpression[] expressions, String name) throws Exception
    {
        super(builder, expressions, name);
    }

    @Override
    protected void verifyArgument(int index, IExpression expression)
    {}

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        if (this.builder instanceof ParticleMolangParser parser && parser.scheme.particle != null)
        {
            String name = this.args[this.args.length > 1 ? 1 : 0].stringValue();
            Particle particle = parser.scheme.particle;

            if (this.args.length > 1)
            {
                 particle = parser.scheme.emitter.getParticleByIndex((int) this.args[0].doubleValue());
            }

            if (particle == null)
            {
                return 0D;
            }

            return particle.localValues.getOrDefault(name, 0D);
        }

        return 0;
    }
}