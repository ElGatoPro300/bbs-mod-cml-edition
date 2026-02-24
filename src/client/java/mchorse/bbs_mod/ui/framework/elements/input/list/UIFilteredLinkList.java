package elgatopro300.bbs_cml.ui.framework.elements.input.list;

import elgatopro300.bbs_cml.utils.NaturalOrderComparator;
import elgatopro300.bbs_cml.utils.resources.FilteredLink;

import java.util.List;
import java.util.function.Consumer;

public class UIFilteredLinkList extends UIList<FilteredLink>
{
    public UIFilteredLinkList(Consumer<List<FilteredLink>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort((a, b) -> NaturalOrderComparator.compare(true, a.toString(), b.toString()));

        return true;
    }
}