package mchorse.bbs_mod.ui.framework.elements.context;

import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.graphs.UIKeyframeGraph;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.utils.UILabel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.interps.CustomInterpolation;
import mchorse.bbs_mod.utils.interps.CustomInterpolationManager;
import mchorse.bbs_mod.utils.interps.Interpolations;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;

import mchorse.bbs_mod.ui.framework.elements.context.UIInterpolationContextMenu;
import mchorse.bbs_mod.ui.framework.elements.context.UISimpleContextMenu;
import mchorse.bbs_mod.ui.utils.context.ContextAction;
import mchorse.bbs_mod.ui.utils.InterpolationUtils;
import mchorse.bbs_mod.ui.utils.icons.Icon;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UICustomInterpolationKeyframes;
import mchorse.bbs_mod.utils.interps.InterpContext;
import java.util.Map;
import java.util.function.Consumer;

public class UICustomInterpolationPanel extends UIOverlayPanel
{
    public UITextbox name;
    public UICustomInterpolationKeyframes keyframes;
    public UIButton save;
    
    private CustomInterpolation interpolation;
    private boolean firstResize = true;
    private Consumer<CustomInterpolation> saveCallback;
    
    public UICustomInterpolationPanel()
    {
        super(UIKeys.INTERPOLATIONS_CUSTOM_TITLE);
        
        this.interpolation = new CustomInterpolation("custom");
        
        this.name = new UITextbox(1000, (t) -> {});
        this.name.setText("custom");
        
        this.keyframes = new UICustomInterpolationKeyframes((k) -> {});
        this.keyframes.single().duration(() -> 1);
        
        // Setup initial keyframes
        KeyframeChannel<Double> channel = new KeyframeChannel<>("interp", KeyframeFactories.DOUBLE);
        channel.insert(0, 0D);
        channel.insert(1, 1D);
        
        for (mchorse.bbs_mod.utils.keyframes.Keyframe keyframe : channel.getKeyframes())
        {
            keyframe.getInterpolation().setInterp(Interpolations.BEZIER);
            keyframe.lx = 0.15f;
            keyframe.rx = 0.15f;
        }
        
        UIKeyframeSheet sheet = new UIKeyframeSheet("interp", IKey.raw("interp"), Colors.ACTIVE, false, channel, null);
        this.keyframes.addSheet(sheet);
        this.keyframes.editSheet(sheet);
        this.keyframes.resetView();
        
        this.save = new UIButton(UIKeys.INTERPOLATIONS_CUSTOM_SAVE, (b) -> this.saveInterpolation());
        
        UILabel label = UI.label(UIKeys.INTERPOLATIONS_CUSTOM_NAME).color(Colors.WHITE, true);
        UIElement sidebar = UI.column(5, 10, label, this.name, this.save);
        
        sidebar.relative(this.content).x(1F).y(0).w(140).h(1F).anchorX(1F);
        this.keyframes.relative(this.content).x(0).y(0).w(1F, -140).h(1F);
        
        this.content.add(sidebar, this.keyframes);
    }
    
    @Override
    public void resize()
    {
        super.resize();
        
        if (this.firstResize)
        {
            this.keyframes.resetView();
            this.firstResize = false;
        }
    }
    
    public void saveInterpolation()
    {
        String name = this.name.getText();
        
        if (name.isEmpty())
        {
            return;
        }
        
        this.interpolation = new CustomInterpolation(name);
        
        if (!this.keyframes.getGraph().getSheets().isEmpty())
        {
             UIKeyframeSheet sheet = this.keyframes.getGraph().getSheets().get(0);
             this.interpolation.channel.fromData(sheet.channel.toData());
        }
        
        CustomInterpolationManager.INSTANCE.save(this.interpolation);

        if (this.saveCallback != null)
        {
            this.saveCallback.accept(this.interpolation);
        }

        this.close();
    }

    public UICustomInterpolationPanel onSave(Consumer<CustomInterpolation> callback)
    {
        this.saveCallback = callback;
        return this;
    }
}
