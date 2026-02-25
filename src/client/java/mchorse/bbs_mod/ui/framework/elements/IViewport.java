package elgatopro300.bbs_cml.ui.framework.elements;

import elgatopro300.bbs_cml.ui.framework.elements.utils.IViewportStack;

public interface IViewport
{
    public void apply(IViewportStack stack);

    public void unapply(IViewportStack stack);
}