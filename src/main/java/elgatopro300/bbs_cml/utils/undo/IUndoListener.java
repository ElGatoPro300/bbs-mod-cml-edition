package elgatopro300.bbs_cml.utils.undo;

public interface IUndoListener<T>
{
    public void handleUndo(IUndo<T> undo, boolean redo);
}