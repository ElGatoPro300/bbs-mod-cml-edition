package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.shapes;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;

public class SquareKeyframeShapeRenderer implements IKeyframeShapeRenderer
{
    @Override
    public IKey getLabel()
    {
        return UIKeys.KEYFRAMES_SHAPES_SQUARE;
    }

    @Override
    public Icon getIcon()
    {
        return Icons.SQUARE;
    }

    @Override
    public void renderKeyframe(UIContext uiContext, BufferBuilder builder, Matrix4f matrix4f, int x, int y, int offset, int c)
    {
        uiContext.batcher.fillRect(builder, matrix4f, x - offset, y - offset, offset * 2, offset * 2, c, c, c, c);
    }
}
