package elgatopro300.bbs_cml.ui.forms.editors.utils;

import elgatopro300.bbs_cml.forms.forms.utils.ParticleSettings;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIListOverlayPanel;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class UIParticleSettings extends UIElement
{
    public UIButton particle;
    public UITextbox arguments;

    private ParticleSettings settings;

    public UIParticleSettings()
    {
        this.particle = new UIButton(UIKeys.FORMS_EDITORS_VANILLA_PARTICLE_EDITOR_PICK, (b) ->
        {
            UIListOverlayPanel overlayPanel = new UIListOverlayPanel(UIKeys.FORMS_EDITORS_VANILLA_PARTICLE_EDITOR_TITLE, (l) -> this.setParticle(Identifier.of(l)));
            List<String> strings = new ArrayList<>();

            for (RegistryKey<ParticleType<?>> key : Registries.PARTICLE_TYPE.getKeys())
            {
                strings.add(key.getValue().toString());
            }

            overlayPanel.addValues(strings);
            overlayPanel.list.list.sort();
            overlayPanel.setValue(this.settings.particle.toString());

            UIOverlay.addOverlay(this.getContext(), overlayPanel);
        });

        this.arguments = new UITextbox(1000, this::setArguments);

        this.column().vertical().stretch();
        this.add(this.particle, this.arguments);
    }

    public void setSettings(ParticleSettings settings)
    {
        this.settings = settings;

        this.arguments.setText(settings.arguments);
    }

    protected void setParticle(Identifier id)
    {
        this.settings.particle = id;
    }

    protected void setArguments(String args)
    {
        this.settings.arguments = args;
    }
}