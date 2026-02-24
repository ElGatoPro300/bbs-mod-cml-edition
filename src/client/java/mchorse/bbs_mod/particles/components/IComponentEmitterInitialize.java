package elgatopro300.bbs_cml.particles.components;

import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public interface IComponentEmitterInitialize extends IComponentBase
{
    public void apply(ParticleEmitter emitter);
}