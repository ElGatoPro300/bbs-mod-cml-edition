package elgatopro300.bbs_cml.ui.framework.elements.input.list;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Label;
import elgatopro300.bbs_cml.utils.NaturalOrderComparator;

import java.util.List;
import java.util.function.Consumer;

public class UILabelList <T> extends UIList<Label<T>>
{
    public UILabelList(Consumer<List<Label<T>>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    public void add(IKey title, T value)
    {
        this.add(new Label<>(title, value));
    }

    public void setCurrentTitle(String title)
    {
        for (int i = 0; i < this.list.size(); i ++)
        {
            if (this.list.get(i).title.equals(title))
            {
                this.setIndex(i);

                return;
            }
        }
    }

    public void setCurrentValue(T value)
    {
        for (int i = 0; i < this.list.size(); i ++)
        {
            if (this.list.get(i).value.equals(value))
            {
                this.setIndex(i);

                return;
            }
        }
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort((a, b) -> NaturalOrderComparator.compare(true, a.title.get(), b.title.get()));

        return true;
    }

    @Override
    protected String elementToString(UIContext context, int i, Label<T> element)
    {
        return element.title.get();
    }
}