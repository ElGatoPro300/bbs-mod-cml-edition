package elgatopro300.bbs_cml.utils.manager.storage;

import elgatopro300.bbs_cml.data.types.MapType;

import java.io.File;
import java.io.IOException;

public interface IDataStorage
{
    public MapType load(File file) throws IOException;

    public void save(File file, MapType data) throws IOException;
}