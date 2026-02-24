package elgatopro300.bbs_cml.utils.manager.storage;

import elgatopro300.bbs_cml.data.DataToString;
import elgatopro300.bbs_cml.data.types.MapType;

import java.io.File;
import java.io.IOException;

public class JSONLikeStorage implements IDataStorage
{
    private boolean json;

    public JSONLikeStorage json()
    {
        this.json = true;

        return this;
    }

    @Override
    public MapType load(File file) throws IOException
    {
        return (MapType) DataToString.read(file);
    }

    @Override
    public void save(File file, MapType data) throws IOException
    {
        DataToString.write(file, data, this.json);
    }
}