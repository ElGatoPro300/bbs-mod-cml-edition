package elgatopro300.bbs_cml.ui.film.utils.undo;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.utils.DataPath;
import elgatopro300.bbs_cml.utils.undo.IUndo;

public class ValueChangeUndo extends FilmEditorUndo
{
    public DataPath name;
    public BaseType oldValue;
    public BaseType newValue;
    public MapType uiBefore;
    public MapType uiAfter;

    private boolean mergable = true;

    public ValueChangeUndo(DataPath name, BaseType oldValue, BaseType newValue)
    {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public MapType getUIData(boolean redo)
    {
        return redo ? this.uiAfter : this.uiBefore;
    }

    public void cacheBefore(MapType uiData)
    {
        this.uiBefore = uiData;
    }

    public void cacheAfter(UIElement editor)
    {
        this.uiAfter = editor.getRoot() == null ? new MapType() : editor.getRoot().collectAllUndoData();
    }

    public DataPath getName()
    {
        return this.name;
    }

    @Override
    public IUndo<ValueGroup> noMerging()
    {
        this.mergable = false;

        return this;
    }

    @Override
    public boolean isMergeable(IUndo<ValueGroup> undo)
    {
        if (!this.mergable)
        {
            return false;
        }

        if (undo instanceof ValueChangeUndo)
        {
            ValueChangeUndo valueUndo = (ValueChangeUndo) undo;

            return this.name.equals(valueUndo.getName());
        }

        return false;
    }

    @Override
    public void merge(IUndo<ValueGroup> undo)
    {
        if (undo instanceof ValueChangeUndo)
        {
            ValueChangeUndo prop = (ValueChangeUndo) undo;

            this.newValue = prop.newValue;
        }
    }

    @Override
    public void undo(ValueGroup context)
    {
        BaseValue value = context.getRecursively(this.name);

        if (value.getPath().equals(this.name))
        {
            value.fromData(this.oldValue);
        }
    }

    @Override
    public void redo(ValueGroup context)
    {
        BaseValue value = context.getRecursively(this.name);

        if (value.getPath().equals(this.name))
        {
            value.fromData(this.newValue);
        }
    }
}