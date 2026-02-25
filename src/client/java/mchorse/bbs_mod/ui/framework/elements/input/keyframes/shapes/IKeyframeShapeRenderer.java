package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.shapes;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;

public interface IKeyframeShapeRenderer
{
    public IKey getLabel();

    public Icon getIcon();

    public void renderKeyframe(UIContext uiContext, BufferBuilder builder, Matrix4f matrix4f, int x, int y, int offset, int c);

    public default void renderKeyframeBackground(UIContext uiContext, BufferBuilder builder, Matrix4f matrix4f, int x, int y, int offset, int c)
    {}
}