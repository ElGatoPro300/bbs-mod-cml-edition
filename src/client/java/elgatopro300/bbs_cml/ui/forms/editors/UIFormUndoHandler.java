package elgatopro300.bbs_cml.ui.forms.editors;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.settings.values.IValueListener;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.ui.film.utils.undo.ValueChangeUndo;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.utils.Timer;
import elgatopro300.bbs_cml.utils.undo.CompoundUndo;
import elgatopro300.bbs_cml.utils.undo.IUndo;
import elgatopro300.bbs_cml.utils.undo.UndoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UIFormUndoHandler
{
    protected UndoManager<ValueGroup> undoManager;

    protected Map<BaseValue, BaseType> cachedValues = new HashMap<>();
    protected boolean cacheMarkLastUndoNoMerging;
    protected MapType uiData;

    protected Timer undoTimer = new Timer(1000);

    protected UIElement uiElement;

    /**
     * Remove any child tree entries if one of the parents is present already.
     * For example, let's say the undo submitted at the same time:
     *
     * - film.clips
     * - film.clips.0
     * - film.clips.0.duration
     *
     * There is no point in caching .0 and .0.duration since films .clips will get
     * cached anyway. Therefore, it's smart to eliminate those from the cache, and
     * submit only films.clips.
     */
    public static void reduceUndoRedundancy(Map<BaseValue, BaseType> cachedValues)
    {
        Iterator<BaseValue> it = cachedValues.keySet().iterator();

        while (it.hasNext())
        {
            BaseValue value = it.next().getParent();
            boolean remove = false;

            while (value != null)
            {
                if (cachedValues.containsKey(value))
                {
                    remove = true;

                    break;
                }

                value = value.getParent();
            }

            if (remove)
            {
                it.remove();
            }
        }
    }

    public UIFormUndoHandler(UIElement uiElement)
    {
        this.uiElement = uiElement;

        this.reset();
    }

    public UndoManager<ValueGroup> getUndoManager()
    {
        return this.undoManager;
    }

    public void reset()
    {
        this.undoManager = new UndoManager<>(100);
        this.undoManager.setCallback(this::handleUndos);
    }

    /**
     * Handle undo/redo. This method primarily updates the UI state, according to
     * the undo/redo changes were done.
     */
    private void handleUndos(IUndo<ValueGroup> undo, boolean redo)
    {
        IUndo<ValueGroup> anotherUndo = undo;

        if (anotherUndo instanceof CompoundUndo)
        {
            anotherUndo = ((CompoundUndo<ValueGroup>) anotherUndo).getFirst(ValueChangeUndo.class);
        }

        if (anotherUndo instanceof ValueChangeUndo)
        {
            ValueChangeUndo change = (ValueChangeUndo) anotherUndo;
            UIElement root = this.uiElement.getRoot();
            if (root != null)
            {
                root.applyAllUndoData(change.getUIData(redo));
            }
        }
    }

    public void handlePreValues(BaseValue baseValue, int flag)
    {
        if (this.uiData == null && this.uiElement.getRoot() != null)
        {
            UIElement root = this.uiElement.getRoot();
            if (root != null)
            {
                this.uiData = root.collectAllUndoData();
            }
        }

        if (!this.cachedValues.containsKey(baseValue))
        {
            this.cachedValues.put(baseValue, baseValue.toData());
        }

        if ((flag & IValueListener.FLAG_UNMERGEABLE) != 0)
        {
            this.cacheMarkLastUndoNoMerging = true;
        }
    }

    public void submitUndo()
    {
        this.handleTimers();

        if (this.cachedValues.isEmpty())
        {
            return;
        }

        reduceUndoRedundancy(this.cachedValues);

        List<ValueChangeUndo> changeUndos = new ArrayList<>();

        for (Map.Entry<BaseValue, BaseType> entry : this.cachedValues.entrySet())
        {
            BaseValue value = entry.getKey();
            ValueChangeUndo undo = new ValueChangeUndo(value.getPath(), entry.getValue(), value.toData());

            undo.cacheAfter(this.uiElement);
            undo.cacheBefore(this.uiData);
            changeUndos.add(undo);

            this.handleValue(value);
        }

        if (changeUndos.size() == 1)
        {
            this.undoManager.pushUndo(changeUndos.get(0));
        }
        else if (!changeUndos.isEmpty())
        {
            this.undoManager.pushUndo(new CompoundUndo<>(changeUndos.toArray(new IUndo[0])));
        }

        this.cachedValues.clear();
        this.uiData = null;

        this.undoTimer.mark();

        if (this.cacheMarkLastUndoNoMerging)
        {
            this.cacheMarkLastUndoNoMerging = false;

            this.undoManager.markLastUndoNoMerging();
        }
    }

    protected void handleValue(BaseValue value)
    {}

    protected void handleTimers()
    {
        if (this.undoTimer.checkReset())
        {
            this.undoManager.markLastUndoNoMerging();
        }
    }
}