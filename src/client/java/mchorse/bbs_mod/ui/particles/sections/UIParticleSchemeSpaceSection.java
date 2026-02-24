package elgatopro300.bbs_cml.ui.particles.sections;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.particles.ParticleScheme;
import elgatopro300.bbs_cml.particles.components.meta.ParticleComponentLocalSpace;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.particles.UIParticleSchemePanel;

public class UIParticleSchemeSpaceSection extends UIParticleSchemeComponentSection<ParticleComponentLocalSpace>
{
    public UIToggle position;
    public UIToggle rotation;
    public UIToggle velocity;
    public UIToggle textureScale;

    public UIParticleSchemeSpaceSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.position = new UIToggle(UIKeys.SNOWSTORM_SPACE_POSITION, (b) ->
        {
            this.component.position = b.getValue();
            this.editor.dirty();
        });

        this.rotation = new UIToggle(UIKeys.SNOWSTORM_SPACE_ROTATION, (b) ->
        {
            this.component.rotation = b.getValue();
            this.editor.dirty();
        });

        this.velocity = new UIToggle(UIKeys.SNOWSTORM_SPACE_VELOCITY, (b) ->
        {
            this.component.velocity = b.getValue();
            this.editor.dirty();
        });

        this.textureScale = new UIToggle(UIKeys.SNOWSTORM_SPACE_TEXTURE_SCALE, (b) ->
        {
            this.component.textureScale = b.getValue();
            this.editor.dirty();
        });

        this.fields.add(this.position, this.rotation, this.velocity, this.textureScale);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_SPACE_TITLE;
    }

    @Override
    protected ParticleComponentLocalSpace getComponent(ParticleScheme scheme)
    {
        return scheme.getOrCreate(ParticleComponentLocalSpace.class);
    }

    @Override
    protected void fillData()
    {
        this.position.setValue(this.component.position);
        this.rotation.setValue(this.component.rotation);
        this.velocity.setValue(this.component.velocity);
        this.textureScale.setValue(this.component.textureScale);
    }
}