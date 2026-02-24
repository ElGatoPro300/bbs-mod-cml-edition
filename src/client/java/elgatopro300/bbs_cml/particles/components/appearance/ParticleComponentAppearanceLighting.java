package elgatopro300.bbs_cml.particles.components.appearance;

import elgatopro300.bbs_cml.particles.components.IComponentEmitterInitialize;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public class ParticleComponentAppearanceLighting extends ParticleComponentBase implements IComponentEmitterInitialize
{
    @Override
    public void apply(ParticleEmitter emitter)
    {
        emitter.lit = false;
    }

    @Override
    public boolean canBeEmpty()
    {
        return true;
    }
}