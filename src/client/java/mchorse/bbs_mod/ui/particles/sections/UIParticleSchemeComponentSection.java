package elgatopro300.bbs_cml.ui.particles.sections;

import elgatopro300.bbs_cml.particles.ParticleScheme;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.ui.particles.UIParticleSchemePanel;

public abstract class UIParticleSchemeComponentSection <T extends ParticleComponentBase> extends UIParticleSchemeSection
{
    protected T component;

    public UIParticleSchemeComponentSection(UIParticleSchemePanel parent)
    {
        super(parent);
    }

    @Override
    public void setScheme(ParticleScheme scheme)
    {
        super.setScheme(scheme);

        this.component = this.getComponent(scheme);
        this.fillData();
    }

    protected abstract T getComponent(ParticleScheme scheme);

    protected void fillData()
    {}
}