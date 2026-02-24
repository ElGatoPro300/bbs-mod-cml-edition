package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.elements.input.color.UIColorPicker;

import java.util.function.Consumer;

public class UIColorOverlayPanel extends UIOverlayPanel
{
    public UIColorPicker picker;

    public UIColorOverlayPanel(IKey title, Consumer<Integer> callback)
    {
        super(title);

        this.picker = new UIColorPicker(callback);
        this.picker.relative(this.content).xy(0.5F, 0.5F).wh(200, 120).anchor(0.5F);
        
        this.content.add(this.picker);
    }
}
