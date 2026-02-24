package elgatopro300.bbs_cml.utils.keyframes.factories;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.forms.utils.StructureLightSettings;
import elgatopro300.bbs_cml.utils.interps.IInterp;

public class StructureLightSettingsKeyframeFactory implements IKeyframeFactory<StructureLightSettings>
{
    private final StructureLightSettings i = new StructureLightSettings();

    @Override
    public StructureLightSettings fromData(BaseType data)
    {
        StructureLightSettings value = new StructureLightSettings();

        if (data.isMap())
        {
            value.fromData(data.asMap());
        }

        return value;
    }

    @Override
    public BaseType toData(StructureLightSettings value)
    {
        return value == null ? new MapType() : value.toData();
    }

    @Override
    public StructureLightSettings createEmpty()
    {
        return new StructureLightSettings();
    }

    @Override
    public StructureLightSettings copy(StructureLightSettings value)
    {
        return value == null ? null : value.copy();
    }

    @Override
    public StructureLightSettings interpolate(StructureLightSettings preA, StructureLightSettings a, StructureLightSettings b, StructureLightSettings postB, IInterp interpolation, float x)
    {
        this.i.enabled = a.enabled;
        int y = (int) Math.round(interpolation.interpolate(IInterp.context.set(preA.intensity, a.intensity, b.intensity, postB.intensity, x)));
        if (y < 0) y = 0; else if (y > 15) y = 15;
        this.i.intensity = y;
        return this.i;
    }
}