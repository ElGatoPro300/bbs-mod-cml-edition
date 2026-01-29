package mchorse.bbs_mod.ui.model;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.cubic.model.ModelConfig;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.ContentType;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIScrollView;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.utils.UILabel;
import mchorse.bbs_mod.ui.utils.Area;
import mchorse.bbs_mod.ui.utils.ScrollDirection;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icon;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.ui.utils.pose.UIPoseEditor;
import mchorse.bbs_mod.utils.Direction;
import mchorse.bbs_mod.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class UIModelPanel extends UIDataDashboardPanel<ModelConfig>
{
    public UIModelEditorRenderer renderer;
    
    public UIElement mainView;
    public UIScrollView sidebar;
    public List<UIElement> panels = new ArrayList<>();
    
    public UIElement modelSettingsPanel;
    public UIScrollView sectionsView;
    public UIScrollView rightView;
    public List<UIModelSection> sections = new ArrayList<>();

    public UIModelPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.renderer = new UIModelEditorRenderer();
        this.renderer.relative(this).wTo(this.iconBar.getFlex()).h(1F);
        this.renderer.setCallback(this::pickBone);
        
        this.prepend(this.renderer);

        /* Sidebar setup - Left side */
        this.sidebar = new UIScrollView(ScrollDirection.VERTICAL);
        this.sidebar.scroll.cancelScrolling().noScrollbar();
        this.sidebar.scroll.scrollSpeed = 5;
        this.sidebar.relative(this.editor).w(20).h(1F).column(0).stretch().scroll();
        
        this.sidebar.preRender((context) ->
        {
            /* Render background matching UIForm style */
            context.batcher.box(this.sidebar.area.x, this.sidebar.area.y, this.sidebar.area.ex(), this.sidebar.area.ey(), Colors.A50);

            /* Active button background */
            for (int i = 0, c = this.panels.size(); i < c; i++)
            {
                if (this.mainView.getChildren().contains(this.panels.get(i)))
                {
                    UIElement child = (UIElement) this.sidebar.getChildren().get(i);
                    if (child instanceof UIIcon) 
                    {
                         Area area = ((UIIcon) child).area;
                         area.render(context.batcher, Colors.A75 | BBSSettings.primaryColor.get());
                    }
                }
            }
        });

        this.mainView = new UIElement();
        this.mainView.relative(this.editor).x(20).w(1F, -20).h(1F);

        this.editor.add(this.mainView, this.sidebar);

        /* Model Settings Panel */
        this.modelSettingsPanel = new UIElement();
        this.modelSettingsPanel.relative(this.mainView).w(1F).h(1F);
        
        this.sectionsView = UI.scrollView(20, 10);
        this.sectionsView.scroll.cancelScrolling().opposite().scrollSpeed *= 3;
        this.sectionsView.relative(this.modelSettingsPanel).w(200).h(1F);
        
        this.rightView = UI.scrollView(20, 10);
        this.rightView.scroll.cancelScrolling().opposite().scrollSpeed *= 3;
        this.rightView.relative(this.modelSettingsPanel).x(1F, -200).w(200).h(1F);
        
        this.modelSettingsPanel.add(this.sectionsView, this.rightView);

        /* Sections setup */
        this.overlay.namesList.setFileIcon(Icons.MORPH);

        this.addSection(new UIModelGeneralSection(this));
        
        UIModelPartsSection parts = new UIModelPartsSection(this);
        this.sections.add(parts);
        this.setRight(parts.poseEditor);
        this.renderer.transform = parts.poseEditor.transform;

        this.addSection(new UIModelArmorSection(this));
        this.addSection(new UIModelItemsSection(this));
        this.addSection(new UIModelHandsSection(this));
        this.addSection(new UIModelSneakingSection(this));
        
        /* Register Panels */
        this.registerPanel(this.modelSettingsPanel, UIKeys.MODELS_SETTINGS, Icons.POSE);
        this.registerPanel(this.createUnavailablePanel(), UIKeys.MODELS_IK_EDITOR, Icons.LIMB);
        this.registerPanel(this.createUnavailablePanel(), UIKeys.MODELS_DYNAMIC_BONES, Icons.SHAPES);

        this.setPanel(this.modelSettingsPanel);
        
        this.fill(null);
    }
    
    private UIElement createUnavailablePanel()
    {
        UIElement panel = new UIElement();
        panel.relative(this.mainView).w(1F).h(1F);
        
        UILabel label = new UILabel(UIKeys.COMING_SOON)
        {
            @Override
            public void render(UIContext context)
            {
                context.batcher.getContext().getMatrices().push();
                
                int cx = this.area.mx();
                int cy = this.area.my();
                
                context.batcher.getContext().getMatrices().translate(cx, cy, 0);
                context.batcher.getContext().getMatrices().scale(2F, 2F, 1F);
                context.batcher.getContext().getMatrices().translate(-cx, -cy, 0);
                
                super.render(context);
                
                context.batcher.getContext().getMatrices().pop();
            }
        }.background();
        
        label.relative(panel).w(1F).xy(0.5F, 0.5F).anchor(0.5F, 0.5F);
        label.labelAnchor(0.5F, 0.5F);
        panel.add(label);
        
        return panel;
    }

    public UIIcon registerPanel(UIElement panel, IKey tooltip, Icon icon)
    {
        UIIcon button = new UIIcon(icon, (b) -> this.setPanel(panel));

        if (tooltip != null)
        {
            button.tooltip(tooltip, Direction.RIGHT);
        }

        this.panels.add(panel);
        this.sidebar.add(button);

        return button;
    }

    public void setPanel(UIElement panel)
    {
        this.mainView.removeAll();
        this.mainView.add(panel);
        this.mainView.resize();
    }
    
    public void setRight(UIElement element)
    {
        this.rightView.removeAll();
        this.rightView.add(element);
        this.rightView.resize();
    }
    
    public UIPoseEditor getPoseEditor()
    {
        for (UIModelSection section : this.sections)
        {
            if (section instanceof UIModelPartsSection)
            {
                return ((UIModelPartsSection) section).poseEditor;
            }
        }

        return null;
    }

    private void pickBone(String bone)
    {
        for (UIModelSection section : this.sections)
        {
            section.deselect();

            if (section instanceof UIModelPartsSection)
            {
                ((UIModelPartsSection) section).selectBone(bone);
                this.setRight(((UIModelPartsSection) section).poseEditor);
            }
        }
    }
    
    public void dirty()
    {
        this.renderer.dirty();
    }

    private void addSection(UIModelSection section)
    {
        this.sections.add(section);
        this.sectionsView.add(section);
    }

    @Override
    public ContentType getType()
    {
        return ContentType.MODELS;
    }

    @Override
    protected IKey getTitle()
    {
        return UIKeys.MODELS_TITLE;
    }

    @Override
    protected void fillData(ModelConfig data)
    {
        if (data != null)
        {
            this.renderer.setModel(data.getId());
            this.renderer.setConfig(data);
            
            for (UIModelSection section : this.sections)
            {
                section.setConfig(data);
            }
            
            this.sectionsView.resize();
            this.rightView.resize();
        }
    }

    @Override
    public void resize()
    {
        super.resize();

        this.renderer.resize();
    }

    @Override
    public void close()
    {}
}