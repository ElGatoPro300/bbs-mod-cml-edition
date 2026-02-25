package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIList;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Colors;

/**
 * General purpose overlay list editor of generic data
 */
public abstract class UIEditorOverlayPanel <T> extends UIOverlayPanel
{
    public UIList<T> list;
    public UIScrollView editor;

    protected T item;

    public UIEditorOverlayPanel(IKey title)
    {
        super(title);

        this.list = this.createList();
        this.list.context((menu) ->
        {
            menu.action(Icons.ADD, this.getAddLabel(), this::addItem);

            if (!this.list.getList().isEmpty())
            {
                menu.action(Icons.REMOVE, this.getRemoveLabel(), Colors.NEGATIVE, this::removeItem);
            }
        });

        this.editor = UI.scrollView(5, 10);

        this.list.relative(this.content).w(120).h(1F);
        this.editor.relative(this.content).x(120).w(1F, -120).h(1F);

        this.content.add(this.editor, this.list);
        this.content.x(6).y(26).w(1F, -32);
    }

    protected abstract UIList<T> createList();

    protected IKey getAddLabel()
    {
        return IKey.EMPTY;
    }

    protected IKey getRemoveLabel()
    {
        return IKey.EMPTY;
    }

    protected void addItem()
    {
        this.addNewItem();
        this.list.update();
    }

    protected void addNewItem()
    {}

    protected void removeItem()
    {
        int index = this.list.getIndex();

        this.list.getList().remove(index);

        index = Math.max(index - 1, 0);
        T item = this.list.getList().isEmpty() ? null : this.list.getList().get(index);

        this.pickItem(item, true);
        this.list.update();
    }

    protected void pickItem(T item, boolean select)
    {
        this.item = item;

        this.editor.setVisible(item != null);

        if (item != null)
        {
            this.fillData(item);

            if (select)
            {
                this.list.setCurrentScroll(item);
            }

            this.resize();
        }
    }

    protected abstract void fillData(T item);
}