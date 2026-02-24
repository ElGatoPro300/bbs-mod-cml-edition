package elgatopro300.bbs_cml.film;

import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.utils.manager.BaseManager;
import elgatopro300.bbs_cml.utils.manager.storage.CompressedDataStorage;

import java.io.File;
import java.util.function.Supplier;

public class FilmManager extends BaseManager<Film>
{
    public FilmManager(Supplier<File> folder)
    {
        super(folder);

        this.backUps = true;
        this.storage = new CompressedDataStorage();
    }

    @Override
    protected Film createData(String id, MapType mapType)
    {
        Film film = new Film();

        if (mapType != null)
        {
            film.fromData(mapType);
        }

        return film;
    }

    @Override
    protected String getExtension()
    {
        return ".dat";
    }
}