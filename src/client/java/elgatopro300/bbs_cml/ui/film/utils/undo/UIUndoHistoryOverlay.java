package elgatopro300.bbs_cml.ui.film.utils.undo;

import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.UIFilmPanel;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.UIUtils;
import elgatopro300.bbs_cml.utils.undo.UndoManager;

public class UIUndoHistoryOverlay extends UIOverlayPanel
{
    private UIUndoList<ValueGroup> list;

    private UIFilmPanel panel;

    public UIUndoHistoryOverlay(UIFilmPanel panel)
    {
        super(UIKeys.FILM_HISTORY_TITLE);

        this.panel = panel;

        this.list = new UIUndoList((l) ->
        {
            int index = this.list.getIndex();
            UndoManager<ValueGroup> undoManager = this.panel.getUndoHandler().getUndoManager();

            while (undoManager.getCurrentUndoIndex() != index)
            {
                if (undoManager.getCurrentUndoIndex() > index)
                {
                    undoManager.undo(this.panel.getData());
                }
                else
                {
                    undoManager.redo(this.panel.getData());
                }
            }

            UIUtils.playClick();
        });
        this.list.setList(this.panel.getUndoHandler().getUndoManager().getUndos());
        this.list.full(this.content);
        this.list.setIndex(this.panel.getUndoHandler().getUndoManager().getCurrentUndoIndex());

        this.content.add(this.list);
    }
}