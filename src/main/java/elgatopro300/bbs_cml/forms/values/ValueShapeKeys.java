package elgatopro300.bbs_cml.forms.values;

import elgatopro300.bbs_cml.obj.shapes.ShapeKeys;
import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class ValueShapeKeys extends BaseKeyframeFactoryValue<ShapeKeys>
{
    public ValueShapeKeys(String id, ShapeKeys value)
    {
        super(id, KeyframeFactories.SHAPE_KEYS, value);
    }
}