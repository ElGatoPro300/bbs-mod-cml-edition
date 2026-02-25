package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.forms.forms.utils.StructureLightSettings;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

public class UIStructureLightSettingsKeyframeFactory extends UIKeyframeFactory<StructureLightSettings>
{
    private UIToggle enabled;
    private UITrackpad intensity;

    public UIStructureLightSettingsKeyframeFactory(Keyframe<StructureLightSettings> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        this.enabled = new UIToggle(UIKeys.FORMS_EDITORS_STRUCTURE_LIGHT, false, (t) -> this.setLightEnabled(t.getValue()));

        this.intensity = new UITrackpad((v) -> this.setIntensity(v.intValue()));
        this.intensity.limit(0, 15).tooltip(UIKeys.FORMS_EDITORS_STRUCTURE_LIGHT_INTENSITY_LABEL);

        UIElement row = UI.row(this.enabled, this.intensity);
        this.scroll.add(row);

        this.update();
    }

    @Override
    public void update()
    {
        super.update();

        StructureLightSettings value = this.keyframe.getValue();
        if (value == null)
        {
            value = new StructureLightSettings(false, 0);
        }

        this.enabled.setValue(value.enabled);
        this.intensity.setValue(value.intensity);
    }

    private void setLightEnabled(boolean v)
    {
        StructureLightSettings value = this.keyframe.getValue();
        if (value == null) value = new StructureLightSettings(false, 0);
        value.enabled = v;
        this.setValue(value);
    }

    private void setIntensity(int v)
    {
        StructureLightSettings value = this.keyframe.getValue();
        if (value == null) value = new StructureLightSettings(false, 0);
        value.intensity = Math.max(0, Math.min(15, v));
        this.setValue(value);
    }
}