package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;

import java.util.Map;

public class RegisterParticleComponentsEvent
{
    public final Map<String, Class<? extends ParticleComponentBase>> components;

    public RegisterParticleComponentsEvent(Map<String, Class<? extends ParticleComponentBase>> components)
    {
        this.components = components;
    }
}
