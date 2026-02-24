package elgatopro300.bbs_cml.utils.pose;

import elgatopro300.bbs_cml.cubic.model.ModelManager;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.utils.presets.DataManager;

public class ShapeKeysManager extends DataManager
{
    public static final ShapeKeysManager INSTANCE = new ShapeKeysManager();

    @Override
    protected Link getFile(String group)
    {
        return Link.assets(ModelManager.MODELS_PREFIX + group + "/shape_keys.json");
    }
}