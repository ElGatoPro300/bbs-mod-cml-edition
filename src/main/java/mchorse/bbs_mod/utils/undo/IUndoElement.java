package elgatopro300.bbs_cml.utils.undo;

import elgatopro300.bbs_cml.data.types.MapType;

public interface IUndoElement
{
    public String getUndoId();

    public void applyUndoData(MapType data);

    public void collectUndoData(MapType data);
}