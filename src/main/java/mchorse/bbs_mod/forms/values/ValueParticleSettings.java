package elgatopro300.bbs_cml.forms.values;

import elgatopro300.bbs_cml.forms.forms.utils.ParticleSettings;
import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueParticleSettings extends BaseKeyframeFactoryValue<ParticleSettings>
{
    public ValueParticleSettings(String id, ParticleSettings value)
    {
        super(id, KeyframeFactories.PARTICLE_SETTINGS, value);
    }
}