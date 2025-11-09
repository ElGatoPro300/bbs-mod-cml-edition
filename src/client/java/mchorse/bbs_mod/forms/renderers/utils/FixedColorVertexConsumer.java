package mchorse.bbs_mod.forms.renderers.utils;

import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;

/**
 * VertexConsumer que fija un color constante (incluido alpha) en el
 * Buffer subyacente mediante {@link VertexConsumer#fixedColor}.
 *
 * Útil para casos donde el renderer nunca llama a {@link VertexConsumer#color},
 * como muchos Block Entity renderers; así la transparencia global se aplica
 * igualmente.
 */
public class FixedColorVertexConsumer implements VertexConsumer
{
    private final VertexConsumer delegate;
    private final Color color;

    public FixedColorVertexConsumer(VertexConsumer delegate, Color color)
    {
        this.delegate = delegate;
        this.color = color;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z)
    {
        return this.delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z)
    {
        return this.delegate.vertex(matrix, x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        int r = (int)(this.color.r * 255f);
        int g = (int)(this.color.g * 255f);
        int b = (int)(this.color.b * 255f);
        int a = (int)(this.color.a * 255f);

        return this.delegate.color(r, g, b, a);
    }

    @Override
    public VertexConsumer texture(float u, float v)
    {
        return this.delegate.texture(u, v);
    }

    @Override
    public VertexConsumer overlay(int overlay)
    {
        return this.delegate.overlay(overlay);
    }

    @Override
    public VertexConsumer overlay(int u, int v)
    {
        return this.delegate.overlay(u, v);
    }

    @Override
    public VertexConsumer light(int light)
    {
        return this.delegate.light(light);
    }

    @Override
    public VertexConsumer light(int u, int v)
    {
        return this.delegate.light(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        return this.delegate.normal(x, y, z);
    }

    @Override
    public VertexConsumer normal(MatrixStack.Entry entry, float x, float y, float z)
    {
        return this.delegate.normal(entry, x, y, z);
    }
}
