package elgatopro300.bbs_cml.ui.framework.elements.utils;

import elgatopro300.bbs_cml.ui.framework.UIContext;

import java.util.function.Consumer;

public class UIClickableText extends UIText
{
    private Consumer<UIClickableText> callback;

    public UIClickableText()
    {
        super();
    }

    public UIClickableText callback(Consumer<UIClickableText> callback)
    {
        this.callback = callback;

        return this;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (context.mouseButton == 0 && this.area.isInside(context))
        {
            if (this.callback != null)
            {
                this.callback.accept(this);
            }

            return true;
        }

        return super.subMouseClicked(context);
    }
}