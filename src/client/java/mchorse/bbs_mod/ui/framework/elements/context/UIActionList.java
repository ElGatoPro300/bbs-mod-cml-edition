package elgatopro300.bbs_cml.ui.framework.elements.context;

import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIList;
import elgatopro300.bbs_cml.ui.utils.context.ContextAction;

import java.util.List;
import java.util.function.Consumer;

public class UIActionList extends UIList<ContextAction>
{
    public UIActionList(Consumer<List<ContextAction>> callback)
    {
        super(callback);
    }

    @Override
    public void renderListElement(UIContext context, ContextAction element, int i, int x, int y, boolean hover, boolean selected)
    {
        int h = this.scroll.scrollItemSize;

        element.render(context, context.batcher.getFont(), x, y, this.area.w, h, hover, selected);
    }
}