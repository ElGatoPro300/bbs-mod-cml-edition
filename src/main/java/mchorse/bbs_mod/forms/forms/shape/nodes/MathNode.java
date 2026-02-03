package mchorse.bbs_mod.forms.forms.shape.nodes;

import mchorse.bbs_mod.data.types.MapType;
import java.util.Arrays;
import java.util.List;

public class MathNode extends ShapeNode
{
    public int operation = 0; // 0: add, 1: sub, 2: mul, 3: div

    public MathNode()
    {}

    @Override
    public String getType()
    {
        return "math";
    }

    @Override
    public List<String> getInputs()
    {
        return Arrays.asList("a", "b");
    }

    @Override
    public List<String> getOutputs()
    {
        return Arrays.asList("result");
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);
        data.putInt("op", this.operation);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);
        this.operation = data.getInt("op");
    }
}
