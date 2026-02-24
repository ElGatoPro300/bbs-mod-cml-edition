package elgatopro300.bbs_cml.ui.utils.resizers;

import elgatopro300.bbs_cml.ui.utils.Area;

public interface IParentResizer
{
    public void apply(Area area, IResizer resizer, ChildResizer child);
}
