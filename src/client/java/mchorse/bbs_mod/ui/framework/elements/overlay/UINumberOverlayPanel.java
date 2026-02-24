package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;

import java.util.function.Consumer;

public class UINumberOverlayPanel extends UIMessageBarOverlayPanel
{
    public UITrackpad value;

    public Consumer<Double> callback;

    public UINumberOverlayPanel(IKey title, IKey message, Consumer<Double> callback)
    {
        super(title, message);

        this.callback = callback;
        this.value = new UITrackpad();

        this.bar.prepend(this.value);
    }

    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);

        parent.getContext().focus(this.value);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        if (this.callback != null)
        {
            this.callback.accept(this.value.getValue());
        }
    }
}
