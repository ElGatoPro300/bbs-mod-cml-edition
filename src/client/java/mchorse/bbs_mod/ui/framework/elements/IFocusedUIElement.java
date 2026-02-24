package elgatopro300.bbs_cml.ui.framework.elements;

import elgatopro300.bbs_cml.ui.framework.UIContext;

public interface IFocusedUIElement
{
    public boolean isFocused();

    public void focus(UIContext context);

    public void unfocus(UIContext context);

    public void selectAll(UIContext context);

    public void unselect(UIContext context);
}