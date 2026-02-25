package elgatopro300.bbs_cml.settings.values.misc;

import elgatopro300.bbs_cml.forms.forms.utils.StructureLightSettings;
import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueStructureLightSettings extends BaseKeyframeFactoryValue<StructureLightSettings>
{
    public ValueStructureLightSettings(String id, StructureLightSettings value)
    {
        super(id, KeyframeFactories.STRUCTURE_LIGHT_SETTINGS, value);
    }
}