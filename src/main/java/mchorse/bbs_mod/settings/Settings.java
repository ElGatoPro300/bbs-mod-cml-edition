package elgatopro300.bbs_cml.settings;

import elgatopro300.bbs_cml.data.DataToString;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import elgatopro300.bbs_cml.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings extends BaseValue
{
    public final Icon icon;
    public final File file;

    public final Map<String, ValueGroup> categories = new LinkedHashMap<>();

    public Settings(Icon icon, String id, File file)
    {
        super(id);

        this.icon = icon;
        this.file = file;
    }

    @Override
    public void postNotify(BaseValue value, int flag)
    {
        this.saveLater();
    }

    /**
     * Get a value from category by their ids
     */
    public BaseValue get(String category, String value)
    {
        ValueGroup cat = this.categories.get(category);

        if (cat != null)
        {
            return cat.get(value);
        }

        return null;
    }

    /**
     * Save later in a separate thread
     */
    public void saveLater()
    {
        SettingsThread.add(this);
    }

    /**
     * Save config to default location
     */
    public void save()
    {
        this.save(this.file);
    }

    /**
     * Save config to given file
     */
    public boolean save(File file)
    {
        try
        {
            if (file != null)
            {
                if (!file.getParentFile().isDirectory())
                {
                    file.getParentFile().mkdirs();
                }

                IOUtils.writeText(file, this.toJson());
            }

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Convert this config into JSON string
     */
    public String toJson()
    {
        return DataToString.toString(this.toData(), true);
    }

    @Override
    public BaseType toData()
    {
        MapType root = new MapType();

        for (Map.Entry<String, ValueGroup> entry : this.categories.entrySet())
        {
            root.put(entry.getKey(), entry.getValue().toData());
        }

        return root;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        for (Map.Entry<String, ValueGroup> entry : this.categories.entrySet())
        {
            entry.getValue().fromData(map.getMap(entry.getKey()));
        }
    }
}