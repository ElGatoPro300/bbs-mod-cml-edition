package elgatopro300.bbs_cml.ui.utils.resizers;

import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.utils.Area;

public interface IResizer
{
    public void preApply(Area area);

    public void apply(Area area);

    public void postApply(Area area);

    public void add(UIElement parent, UIElement child);

    public void remove(UIElement parent, UIElement child);

    public int getX();

    public int getY();

    public int getW();

    public int getH();
}