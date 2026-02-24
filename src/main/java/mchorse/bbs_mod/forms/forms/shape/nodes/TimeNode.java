package elgatopro300.bbs_cml.forms.forms.shape.nodes;

import java.util.Collections;
import java.util.List;

public class TimeNode extends ShapeNode
{
    public TimeNode()
    {}

    @Override
    public String getType()
    {
        return "time";
    }

    @Override
    public List<String> getOutputs()
    {
        return Collections.singletonList("time");
    }
}
