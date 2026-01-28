package mchorse.bbs_mod.ui.model;

import mchorse.bbs_mod.cubic.model.ModelConfig;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.ContentType;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs_mod.ui.framework.elements.UIScrollView;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.ui.utils.pose.UIPoseEditor;

import java.util.ArrayList;
import java.util.List;

public class UIModelPanel extends UIDataDashboardPanel<ModelConfig>
{
    public UIModelEditorRenderer renderer;
    public UIScrollView sectionsView;
    public UIScrollView rightView;
    public List<UIModelSection> sections = new ArrayList<>();

    public UIModelPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.renderer = new UIModelEditorRenderer();
        this.renderer.relative(this).wTo(this.iconBar.getFlex()).h(1F);
        this.renderer.setCallback(this::pickBone);

        this.sectionsView = UI.scrollView(20, 10);
        this.sectionsView.scroll.cancelScrolling().opposite().scrollSpeed *= 3;
        this.sectionsView.relative(this.editor).w(200).h(1F);

        this.rightView = UI.scrollView(20, 10);
        this.rightView.scroll.cancelScrolling().opposite().scrollSpeed *= 3;
        this.rightView.relative(this.editor).x(1F, -200).w(200).h(1F);
        
        this.prepend(this.renderer);
        
        this.editor.add(this.sectionsView, this.rightView);
        
        this.overlay.namesList.setFileIcon(Icons.MORPH);

        this.addSection(new UIModelGeneralSection(this));
        
        UIModelPartsSection parts = new UIModelPartsSection(this);
        this.sections.add(parts);
        this.setRight(parts.poseEditor);
        this.renderer.transform = parts.poseEditor.transform;

        this.addSection(new UIModelArmorSection(this));
        this.addSection(new UIModelItemsSection(this));
        this.addSection(new UIModelSneakingSection(this));
        
        this.fill(null);
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
