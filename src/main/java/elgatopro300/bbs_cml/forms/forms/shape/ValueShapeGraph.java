package elgatopro300.bbs_cml.forms.forms.shape;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;

public class ValueShapeGraph extends BaseValue
{
    private ShapeFormGraph graph;

    public ValueShapeGraph(String id)
    {
        this(id, new ShapeFormGraph());
    }

    public ValueShapeGraph(String id, ShapeFormGraph graph)
    {
        super(id);
        this.graph = graph;
    }

    public ShapeFormGraph get()
    {
        return this.graph;
    }

    public void set(ShapeFormGraph graph)
    {
        this.graph = graph;
        this.postNotify();
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();
        this.graph.toData(data);
        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isMap())
        {
            this.graph.fromData(data.asMap());
        }
    }
}
