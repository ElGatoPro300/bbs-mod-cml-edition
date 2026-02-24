package elgatopro300.bbs_cml.ui.framework.elements.events;

import elgatopro300.bbs_cml.ui.framework.elements.UIElement;

public abstract class UIEvent <T extends UIElement>
{
    public T element;

    public UIEvent(T element)
    {
        this.element = element;
    }
}