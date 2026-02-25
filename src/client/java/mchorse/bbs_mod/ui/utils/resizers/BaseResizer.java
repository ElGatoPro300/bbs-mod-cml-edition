package elgatopro300.bbs_cml.ui.utils.resizers;

import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.utils.Area;

public abstract class BaseResizer implements IResizer, IParentResizer
{
    @Override
    public void preApply(Area area)
    {}

    @Override
    public void apply(Area area)
    {}

    @Override
    public void apply(Area area, IResizer resizer, ChildResizer child)
    {}

    @Override
    public void postApply(Area area)
    {}

    @Override
    public void add(UIElement parent, UIElement child)
    {}

    @Override
    public void remove(UIElement parent, UIElement child)
    {}
}