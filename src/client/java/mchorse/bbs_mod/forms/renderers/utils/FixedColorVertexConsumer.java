package mchorse.bbs_mod.forms.renderers.utils;

import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.render.VertexConsumer;
<<<<<<< HEAD
import org.joml.Matrix4f;
=======
>>>>>>> master

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
<<<<<<< HEAD
    private final int r, g, b, a;
=======
>>>>>>> master

    public FixedColorVertexConsumer(VertexConsumer delegate, Color color)
    {
        this.delegate = delegate;
        this.color = color;
<<<<<<< HEAD
        this.r = (int)(color.r * 255f);
        this.g = (int)(color.g * 255f);
        this.b = (int)(color.b * 255f);
        this.a = (int)(color.a * 255f);
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z)
    {
        // Inyectar color fijo por vértice
        return this.delegate.vertex(x, y, z).color(r, g, b, a);
    }

    @Override
    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z)
    {
        // Inyectar color fijo por vértice
        return this.delegate.vertex(matrix, x, y, z).color(r, g, b, a);
=======

        // Fijar color/alpha global al iniciar
        int r = (int)(color.r * 255f);
        int g = (int)(color.g * 255f);
        int b = (int)(color.b * 255f);
        int a = (int)(color.a * 255f);
        this.delegate.fixedColor(r, g, b, a);
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        return this.delegate.vertex(x, y, z);
>>>>>>> master
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        // Con fixedColor activo, este valor no se usará; delegar por seguridad
        return this.delegate.color(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer texture(float u, float v)
    {
        return this.delegate.texture(u, v);
    }

    @Override
    public VertexConsumer overlay(int u, int v)
    {
        return this.delegate.overlay(u, v);
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

<<<<<<< HEAD
    // Métodos fixedColor/unfixColor no existen en MC 1.21; la inyección se hace en vertex().
}
=======
    @Override
    public void next()
    {
        this.delegate.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha)
    {
        this.delegate.fixedColor(red, green, blue, alpha);
    }

    @Override
    public void unfixColor()
    {
        this.delegate.unfixColor();
    }
}
>>>>>>> master
