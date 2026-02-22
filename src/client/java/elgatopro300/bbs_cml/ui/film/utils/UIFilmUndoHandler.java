package elgatopro300.bbs_cml.ui.film.utils;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.network.ClientNetwork;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.ui.film.UIFilmPanel;
import elgatopro300.bbs_cml.ui.film.utils.undo.ValueChangeUndo;
import elgatopro300.bbs_cml.ui.forms.editors.UIFormUndoHandler;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.utils.Timer;
import elgatopro300.bbs_cml.utils.clips.Clips;
import elgatopro300.bbs_cml.utils.undo.CompoundUndo;
import elgatopro300.bbs_cml.utils.undo.IUndo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UIFilmUndoHandler extends UIFormUndoHandler
{
    private Timer actionsTimer = new Timer(100);
    private Set<BaseValue> syncData = new HashSet<>();
    private boolean isUndoing;

    public UIFilmUndoHandler(UIFilmPanel panel)
    {
        super(panel);
    }

    public boolean undo(ValueGroup context)
    {
        this.isUndoing = true;

        try
        {
            return this.undoManager.undo(context);
        }
        finally
        {
            this.isUndoing = false;
        }
    }

    public boolean redo(ValueGroup context)
    {
        this.isUndoing = true;

        try
        {
            return this.undoManager.redo(context);
        }
        finally
        {
            this.isUndoing = false;
        }
    }

    @Override
    public void reset()
    {
        super.reset();

        this.undoManager.setCallback(this::handleFilmUndos);
    }

    private void handleFilmUndos(IUndo<ValueGroup> undo, boolean redo)
    {
        this.isUndoing = true;

        try
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            this.isUndoing = false;
        }
    }

    @Override
    public void handlePreValues(BaseValue baseValue, int flag)
    {
        if (this.isUndoing)
        {
            return;
        }

        super.handlePreValues(baseValue, flag);
    }

    @Override
    protected void handleValue(BaseValue value)
    {
        if (!this.isUndoing)
        {
            super.handleValue(value);
        }

        if (this.isReplayActions(value))
        {
            this.syncData.add(value);
            this.actionsTimer.mark();
        }
    }

    @Override
    protected void handleTimers()
    {
        super.handleTimers();

        if (this.actionsTimer.checkReset())
        {
            for (BaseValue syncData : this.syncData)
            {
                ClientNetwork.sendSyncData(((UIFilmPanel) this.uiElement).getData().getId(), syncData);
            }

            this.syncData.clear();
        }
    }

    private boolean isReplayActions(BaseValue value)
    {
        String path = value.getPath().toString();

        if (
            path.endsWith("/replays") ||
            path.endsWith("/keyframes") ||
            path.contains("/keyframes/x") ||
            path.contains("/keyframes/y") ||
            path.contains("/keyframes/z") ||
            path.contains("/keyframes/item_main_hand") ||
            path.contains("/keyframes/item_off_hand") ||
            path.contains("/keyframes/item_head") ||
            path.contains("/keyframes/item_chest") ||
            path.contains("/keyframes/item_legs") ||
            path.contains("/keyframes/item_feet") ||
            path.endsWith("/actor") ||
            path.endsWith("/enabled") ||
            path.endsWith("/form") ||
            path.endsWith("/drop_velocity_min_x") ||
            path.endsWith("/drop_velocity_max_x") ||
            path.endsWith("/drop_velocity_min_y") ||
            path.endsWith("/drop_velocity_max_y") ||
            path.endsWith("/drop_velocity_min_z") ||
            path.endsWith("/drop_velocity_max_z")
        ) {
            return true;
        }

        /* Specifically for overwriting full replay like what's done when recording
         * data in the world! */
        if (value.getParent() != null && value.getParent().getId().equals("replays"))
        {
            return true;
        }

        while (value != null)
        {
            if (value instanceof Clips clips && clips.getFactory() == BBSMod.getFactoryActionClips())
            {
                return true;
            }

            value = value.getParent();
        }

        return false;
    }
}