package elgatopro300.bbs_cml.ui.particles;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.data.DataToString;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.renderers.ParticleFormRenderer;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.ParticleScheme;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.ContentType;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.UIDataDashboardPanel;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextEditor;
import elgatopro300.bbs_cml.ui.framework.elements.utils.FontRenderer;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIRenderable;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeAppearanceSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeCollisionSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeCurvesSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeExpirationSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeGeneralSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeInitializationSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeLifetimeSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeLightingSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeMotionSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeRateSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeShapeSection;
import elgatopro300.bbs_cml.ui.particles.sections.UIParticleSchemeSpaceSection;
import elgatopro300.bbs_cml.ui.particles.utils.MolangSyntaxHighlighter;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.Direction;
import elgatopro300.bbs_cml.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIParticleSchemePanel extends UIDataDashboardPanel<ParticleScheme>
{
    /**
     * Default particle placeholder that comes with the engine.
     */
    public static final Link PARTICLE_PLACEHOLDER = Link.assets("particles/default_placeholder.json");

    public UITextEditor textEditor;
    public UIParticleSchemeRenderer renderer;
    public UIScrollView sectionsView;

    public List<UIParticleSchemeSection> sections = new ArrayList<>();

    private String molangId;

    public UIParticleSchemePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.renderer = new UIParticleSchemeRenderer();
        this.renderer.relative(this).wTo(this.iconBar.getFlex()).h(1F);

        this.textEditor = new UITextEditor(null).highlighter(new MolangSyntaxHighlighter());
        this.textEditor.background().relative(this.editor).y(1F, -60).w(1F).h(60);
        this.sectionsView = UI.scrollView(20, 10);
        this.sectionsView.scroll.cancelScrolling().opposite().scrollSpeed *= 3;
        this.sectionsView.relative(this.editor).w(200).hTo(this.textEditor.area);

        this.prepend(new UIRenderable(this::drawOverlay));
        this.prepend(this.renderer);
        this.editor.add(this.textEditor, this.sectionsView);

        UIIcon close = new UIIcon(Icons.CLOSE, (b) -> this.editMoLang(null, null, null));

        close.relative(this.textEditor).x(1F, -20);
        this.textEditor.add(close);
        this.overlay.namesList.setFileIcon(Icons.PARTICLE);

        UIIcon restart = new UIIcon(Icons.REFRESH, (b) ->
        {
            this.renderer.setScheme(this.data);
        });
        restart.tooltip(UIKeys.SNOWSTORM_RESTART_EMITTER, Direction.LEFT);

        this.iconBar.add(restart);

        this.addSection(new UIParticleSchemeGeneralSection(this));
        this.addSection(new UIParticleSchemeCurvesSection(this));
        this.addSection(new UIParticleSchemeSpaceSection(this));
        this.addSection(new UIParticleSchemeInitializationSection(this));
        this.addSection(new UIParticleSchemeRateSection(this));
        this.addSection(new UIParticleSchemeLifetimeSection(this));
        this.addSection(new UIParticleSchemeShapeSection(this));
        this.addSection(new UIParticleSchemeMotionSection(this));
        this.addSection(new UIParticleSchemeExpirationSection(this));
        this.addSection(new UIParticleSchemeAppearanceSection(this));
        this.addSection(new UIParticleSchemeLightingSection(this));
        this.addSection(new UIParticleSchemeCollisionSection(this));

        this.fill(null);
    }

    public void editMoLang(String id, Consumer<String> callback, MolangExpression expression)
    {
        this.molangId = id;
        this.textEditor.callback = callback;
        this.textEditor.setText(expression == null ? "" : expression.toString());
        this.textEditor.setVisible(callback != null);

        if (callback != null)
        {
            this.sectionsView.hTo(this.textEditor.area);
        }
        else
        {
            this.sectionsView.h(1F);
        }

        this.sectionsView.resize();
    }

    @Override
    protected IKey getTitle()
    {
        return UIKeys.SNOWSTORM_TITLE;
    }

    @Override
    public ContentType getType()
    {
        return ContentType.PARTICLES;
    }

    public void dirty()
    {
        this.renderer.emitter.setupVariables();
    }

    private void addSection(UIParticleSchemeSection section)
    {
        this.sections.add(section);
        this.sectionsView.add(section);
    }

    @Override
    protected void fillData(ParticleScheme data)
    {
        this.editMoLang(null, null, null);

        if (this.data != null)
        {
            this.renderer.setScheme(this.data);

            for (UIParticleSchemeSection section : this.sections)
            {
                section.setScheme(this.data);
            }

            this.sectionsView.resize();
        }
    }

    @Override
    public void forceSave()
    {
        super.forceSave();

        ParticleFormRenderer.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void fillDefaultData(ParticleScheme data)
    {
        super.fillDefaultData(data);

        try (InputStream asset = BBSMod.getProvider().getAsset(PARTICLE_PLACEHOLDER))
        {
            MapType map = DataToString.mapFromString(IOUtils.readText(asset));

            ParticleScheme.PARSER.fromData(data, map);
        }
        catch (Exception e)
        {}
    }

    @Override
    public void appear()
    {
        super.appear();

        this.textEditor.updateHighlighter();
    }

    @Override
    public void close()
    {
        if (this.renderer.emitter != null)
        {
            this.renderer.emitter.particles.clear();
        }
    }

    @Override
    public void resize()
    {
        super.resize();

        /* Renderer needs to be resized again because iconBar is in front, and wTo() doesn't
         * work earlier for some reason... */
        this.renderer.resize();
    }

    private void drawOverlay(UIContext context)
    {
        /* Draw debug info */
        if (this.editor.isVisible())
        {
            ParticleEmitter emitter = this.renderer.emitter;
            String label = emitter.particles.size() + "P - " + emitter.age + "A";

            int y = (this.textEditor.isVisible() ? this.textEditor.area.y : this.area.ey()) - 12;

            context.batcher.textShadow(label, this.editor.area.ex() - 4 - context.batcher.getFont().getWidth(label), y);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.molangId != null)
        {
            FontRenderer font = context.batcher.getFont();
            int w = font.getWidth(this.molangId);

            context.batcher.textCard(this.molangId, this.textEditor.area.ex() - 6 - w, this.textEditor.area.ey() - 6 - font.getHeight());
        }
    }
}