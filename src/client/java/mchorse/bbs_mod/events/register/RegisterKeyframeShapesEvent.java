package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.shapes.IKeyframeShapeRenderer;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeShape;
import java.util.Map;

public class RegisterKeyframeShapesEvent
{
    private final Map<KeyframeShape, IKeyframeShapeRenderer> shapes;

    public RegisterKeyframeShapesEvent(Map<KeyframeShape, IKeyframeShapeRenderer> shapes)
    {
        this.shapes = shapes;
    }

    public void register(KeyframeShape shape, IKeyframeShapeRenderer renderer)
    {
        this.shapes.put(shape, renderer);
    }
}
