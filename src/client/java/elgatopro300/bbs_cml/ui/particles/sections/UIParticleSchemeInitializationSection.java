package elgatopro300.bbs_cml.ui.particles.sections;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.particles.ParticleScheme;
import elgatopro300.bbs_cml.particles.components.meta.ParticleComponentInitialization;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.particles.UIParticleSchemePanel;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIParticleSchemeInitializationSection extends UIParticleSchemeComponentSection<ParticleComponentInitialization>
{
    public UIButton create;
    public UIButton update;

    public UIParticleSchemeInitializationSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.create = new UIButton(UIKeys.SNOWSTORM_INITIALIZATION_CREATION, (b) -> this.editMoLang("initialization.create", (str) -> this.component.creation = this.parse(str, this.component.creation), this.component.creation));
        this.create.tooltip(UIKeys.SNOWSTORM_INITIALIZATION_CREATION_TOOLTIP);
        this.update = new UIButton(UIKeys.SNOWSTORM_INITIALIZATION_UPDATE, (b) -> this.editMoLang("initialization.update", (str) -> this.component.update = this.parse(str, this.component.update), this.component.update));
        this.update.tooltip(UIKeys.SNOWSTORM_INITIALIZATION_UPDATE_TOOLTIP);

        this.fields.add(UI.row(this.create, this.update));
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_INITIALIZATION_TITLE;
    }

    @Override
    protected ParticleComponentInitialization getComponent(ParticleScheme scheme)
    {
        return this.scheme.getOrCreate(ParticleComponentInitialization.class);
    }
}