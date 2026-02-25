package elgatopro300.bbs_cml.utils.manager.storage;

import elgatopro300.bbs_cml.data.storage.DataFileStorage;
import elgatopro300.bbs_cml.data.storage.DataGzipStorage;
import elgatopro300.bbs_cml.data.types.MapType;

import java.io.File;
import java.io.IOException;

public class CompressedDataStorage implements IDataStorage
{
    @Override
    public MapType load(File file) throws IOException
    {
        return (MapType) createStorage(file).read();
    }

    @Override
    public void save(File file, MapType data) throws IOException
    {
        this.createStorage(file).write(data);
    }

    private DataGzipStorage createStorage(File file)
    {
        return new DataGzipStorage(new DataFileStorage(file));
    }
}