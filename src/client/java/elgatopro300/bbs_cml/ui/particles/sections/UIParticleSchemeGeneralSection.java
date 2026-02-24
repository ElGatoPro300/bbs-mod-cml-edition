package elgatopro300.bbs_cml.ui.particles.sections;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.graphics.texture.Texture;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.particles.ParticleMaterial;
import elgatopro300.bbs_cml.particles.ParticleScheme;
import elgatopro300.bbs_cml.particles.components.appearance.ParticleComponentAppearanceBillboard;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UICirculate;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITexturePicker;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.particles.UIParticleSchemePanel;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIParticleSchemeGeneralSection extends UIParticleSchemeSection
{
    public UITextbox identifier;
    public UIButton pick;
    public UICirculate material;

    public UIParticleSchemeGeneralSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.identifier = new UITextbox(100, (str) ->
        {
            this.scheme.identifier = str;
            this.editor.dirty();
        });
        this.identifier.tooltip(UIKeys.SNOWSTORM_GENERAL_IDENTIFIER);

        this.pick = new UIButton(UIKeys.SNOWSTORM_GENERAL_PICK, (b) ->
        {
            UITexturePicker.open(this.getContext(), this.scheme.texture, (link) ->
            {
                if (link == null)
                {
                    link = ParticleScheme.DEFAULT_TEXTURE;
                }

                this.setTextureSize(link);
                this.scheme.texture = link;
                this.editor.dirty();
            });
        });

        this.material = new UICirculate((b) ->
        {
            this.scheme.material = ParticleMaterial.values()[this.material.getValue()];
            this.editor.dirty();
        });
        this.material.addLabel(UIKeys.SNOWSTORM_GENERAL_PARTICLES_OPAQUE);
        this.material.addLabel(UIKeys.SNOWSTORM_GENERAL_PARTICLES_ALPHA);
        this.material.addLabel(UIKeys.SNOWSTORM_GENERAL_PARTICLES_BLEND);

        this.fields.add(this.identifier, UI.row(5, 0, 20, this.pick, this.material));
    }

    private void setTextureSize(Link link)
    {
        ParticleComponentAppearanceBillboard component = this.scheme.get(ParticleComponentAppearanceBillboard.class);

        if (component == null)
        {
            return;
        }

        Texture texture = BBSModClient.getTextures().getTexture(link);

        component.textureWidth = texture.width;
        component.textureHeight = texture.height;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_GENERAL_TITLE;
    }

    @Override
    public void setScheme(ParticleScheme scheme)
    {
        super.setScheme(scheme);

        this.identifier.setText(scheme.identifier);
        this.material.setValue(scheme.material.ordinal());
    }
}