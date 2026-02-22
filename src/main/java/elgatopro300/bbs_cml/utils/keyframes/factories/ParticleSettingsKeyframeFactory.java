package elgatopro300.bbs_cml.utils.keyframes.factories;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.forms.forms.utils.ParticleSettings;
import elgatopro300.bbs_cml.utils.interps.IInterp;

public class ParticleSettingsKeyframeFactory implements IKeyframeFactory<ParticleSettings>
{
    @Override
    public ParticleSettings fromData(BaseType data)
    {
        ParticleSettings settings = new ParticleSettings();

        if (data.isMap())
        {
            settings.fromData(data.asMap());
        }

        return settings;
    }

    @Override
    public BaseType toData(ParticleSettings value)
    {
        return value.toData();
    }

    @Override
    public ParticleSettings createEmpty()
    {
        return new ParticleSettings();
    }

    @Override
    public ParticleSettings copy(ParticleSettings value)
    {
        ParticleSettings configs = new ParticleSettings();

        configs.fromData(value.toData());

        return configs;
    }

    @Override
    public ParticleSettings interpolate(ParticleSettings preA, ParticleSettings a, ParticleSettings b, ParticleSettings postB, IInterp interpolation, float x)
    {
        return a;
    }
}