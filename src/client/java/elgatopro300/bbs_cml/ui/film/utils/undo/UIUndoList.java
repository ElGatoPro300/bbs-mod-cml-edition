package elgatopro300.bbs_cml.ui.film.utils.undo;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIList;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.utils.CollectionUtils;
import elgatopro300.bbs_cml.utils.StringUtils;
import elgatopro300.bbs_cml.utils.undo.CompoundUndo;
import elgatopro300.bbs_cml.utils.undo.IUndo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIUndoList <T> extends UIList<IUndo<T>>
{
    public UIUndoList(Consumer<List<IUndo<T>>> callback)
    {
        super(callback);

        this.background();
        this.tooltip(IKey.EMPTY);
    }

    @Override
    protected String elementToString(UIContext context, int i, IUndo<T> element)
    {
        if (element instanceof ValueChangeUndo undo)
        {
            return undo.name.toString();
        }
        else if (element instanceof CompoundUndo<T> compoundUndo)
        {
            List<String> keys = new ArrayList<>();

            for (IUndo<T> undo : compoundUndo.getUndos())
            {
                if (undo instanceof ValueChangeUndo valueUndo)
                {
                    keys.add(valueUndo.name.toString());
                }
            }

            String prefix = StringUtils.findCommonPrefix(keys);

            if (prefix.endsWith("."))
            {
                prefix = prefix.substring(0, prefix.length() - 1);
            }

            return prefix + " (" + compoundUndo.getUndos().size() + ")";
        }

        return super.elementToString(context, i, element);
    }

    @Override
    public void renderTooltip(UIContext context, Area area)
    {
        int index = this.getHoveredIndex(context);
        IUndo<T> safe = CollectionUtils.getSafe(this.getList(), index);

        if (safe != null)
        {
            String label = this.elementToString(context, index, safe);

            context.batcher.textCard(label, context.mouseX + 5, context.mouseY + 5);
        }
    }
}