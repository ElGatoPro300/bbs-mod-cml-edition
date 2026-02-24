package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;

import java.util.function.Consumer;

public class UIPromptOverlayPanel extends UIMessageBarOverlayPanel
{
    public UITextbox text;

    public Consumer<String> callback;

    public UIPromptOverlayPanel(IKey title, IKey message)
    {
        this(title, message, null);
    }

    public UIPromptOverlayPanel(IKey title, IKey message, Consumer<String> callback)
    {
        super(title, message);

        this.callback = callback;
        this.text = new UITextbox(null);

        this.bar.prepend(this.text);
    }

    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);

        this.text.textbox.moveCursorToEnd();
        parent.getContext().focus(this.text);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        if (this.callback != null)
        {
            this.callback.accept(this.text.getText());
        }
    }
}
