package elgatopro300.bbs_cml.particles.components;

import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;

public interface IComponentParticleInitialize extends IComponentBase
{
    public void apply(ParticleEmitter emitter, Particle particle);
}