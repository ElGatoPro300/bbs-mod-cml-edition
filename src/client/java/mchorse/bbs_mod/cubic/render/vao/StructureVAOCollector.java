package mchorse.bbs_mod.cubic.render.vao;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects block model vertices emitted via VertexConsumer and converts quads to triangles,
 * producing arrays suitable for {@link ModelVAO} upload.
 */
public class StructureVAOCollector implements VertexConsumer
{
    private static final class FloatBuf
    {
        float[] data;
        int size;

        FloatBuf(int initial)
        {
            data = new float[Math.max(16, initial)];
            size = 0;
        }

        void ensure(int add)
        {
            int need = size + add;
            if (need > data.length)
            {
                int cap = Math.max(need, data.length + (data.length >>> 1));
                float[] n = new float[cap];
                System.arraycopy(data, 0, n, 0, size);
                data = n;
            }
        }

        void add(float v)
        {
            ensure(1);
            data[size++] = v;
        }

        void add3(float a, float b, float c)
        {
            ensure(3);
            data[size++] = a;
            data[size++] = b;
            data[size++] = c;
        }

        void add2(float a, float b)
        {
            ensure(2);
            data[size++] = a;
            data[size++] = b;
        }

        void add4(float a, float b, float c, float d)
        {
            ensure(4);
            data[size++] = a;
            data[size++] = b;
            data[size++] = c;
            data[size++] = d;
        }

        float[] toArray()
        {
            float[] out = new float[size];
            System.arraycopy(data, 0, out, 0, size);
            return out;
        }
    }
    private static class Vtx
    {
        float x, y, z;
        float nx, ny, nz;
        float u, v;
    }

    private final FloatBuf positions = new FloatBuf(8192);
    private final FloatBuf normals = new FloatBuf(8192);
    private final FloatBuf texCoords = new FloatBuf(8192);
    private final FloatBuf tangents = new FloatBuf(8192);

    private final Vtx[] quad = new Vtx[4];
    private int quadIndex = 0;
    private boolean hasCurrent = false;

    // working per-vertex state until next()
    private float vx, vy, vz;
    private float vnx, vny, vnz;
    private float vu, vv;
    private boolean computeTangents = true;
    private final float[] tangentTmp = new float[3];

    public StructureVAOCollector()
    {
        for (int i = 0; i < 4; i++) quad[i] = new Vtx();
    }

    public void setComputeTangents(boolean computeTangents)
    {
        this.computeTangents = computeTangents;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z)
    {
        // Finalizar vértice previo si existía
        if (hasCurrent) finalizeCurrent();
        this.vx = x;
        this.vy = y;
        this.vz = z;
        this.hasCurrent = true;
        return this;
    }

    @Override
    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z)
    {
        // Finalizar vértice previo si existía
        if (hasCurrent) finalizeCurrent();
        Vector4f v = new Vector4f(x, y, z, 1F);
        v.mul(matrix);
        this.vx = v.x;
        this.vy = v.y;
        this.vz = v.z;
        this.hasCurrent = true;
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        // Per-vertex color is not used; global color is provided via shader attribute.
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v)
    {
        this.vu = u;
        this.vv = v;
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v)
    {
        // Overlay provided via shader attribute; ignore per-vertex overlay.
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v)
    {
        // Lightmap provided via shader attribute; ignore per-vertex light.
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        this.vnx = x;
        this.vny = y;
        this.vnz = z;
        return this;
    }

    private void finalizeCurrent()
    {
        Vtx v = quad[quadIndex];
        v.x = vx; v.y = vy; v.z = vz;
        v.nx = vnx; v.ny = vny; v.nz = vnz;
        v.u = vu; v.v = vv;

        quadIndex++;

        if (quadIndex == 4)
        {
            // Triangulate quad: (0,1,2) and (0,2,3)
            emitTriangle(quad[0], quad[1], quad[2]);
            emitTriangle(quad[0], quad[2], quad[3]);
            quadIndex = 0;
        }
    }

    private void emitTriangle(Vtx a, Vtx b, Vtx c)
    {
        positions.add3(a.x, a.y, a.z);
        positions.add3(b.x, b.y, b.z);
        positions.add3(c.x, c.y, c.z);

        normals.add3(a.nx, a.ny, a.nz);
        normals.add3(b.nx, b.ny, b.nz);
        normals.add3(c.nx, c.ny, c.nz);

        texCoords.add2(a.u, a.v);
        texCoords.add2(b.u, b.v);
        texCoords.add2(c.u, c.v);

        if (computeTangents)
        {
            float[] t = computeTriangleTangent(a, b, c);
            tangents.add4(t[0], t[1], t[2], 1F);
            tangents.add4(t[0], t[1], t[2], 1F);
            tangents.add4(t[0], t[1], t[2], 1F);
        }
        else
        {
            tangents.add4(1F, 0F, 0F, 1F);
            tangents.add4(1F, 0F, 0F, 1F);
            tangents.add4(1F, 0F, 0F, 1F);
        }
    }

    private float[] computeTriangleTangent(Vtx a, Vtx b, Vtx c)
    {
        float x1 = b.x - a.x, y1 = b.y - a.y, z1 = b.z - a.z;
        float x2 = c.x - a.x, y2 = c.y - a.y, z2 = c.z - a.z;
        float u1 = b.u - a.u, v1 = b.v - a.v;
        float u2 = c.u - a.u, v2 = c.v - a.v;

        float denom = (u1 * v2 - u2 * v1);
        if (Math.abs(denom) < 1e-8f)
        {
            float len = (float) Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
            if (len < 1e-8f) { tangentTmp[0] = 1F; tangentTmp[1] = 0F; tangentTmp[2] = 0F; return tangentTmp; }
            tangentTmp[0] = x1 / len; tangentTmp[1] = y1 / len; tangentTmp[2] = z1 / len; return tangentTmp;
        }

        float f = 1.0f / denom;
        float tx = f * (v2 * x1 - v1 * x2);
        float ty = f * (v2 * y1 - v1 * y2);
        float tz = f * (v2 * z1 - v1 * z2);

        float len = (float) Math.sqrt(tx * tx + ty * ty + tz * tz);
        if (len < 1e-8f) { tangentTmp[0] = 1F; tangentTmp[1] = 0F; tangentTmp[2] = 0F; return tangentTmp; }
        tangentTmp[0] = tx / len; tangentTmp[1] = ty / len; tangentTmp[2] = tz / len; return tangentTmp;
    }

    public void fixedColor(int red, int green, int blue, int alpha)
    {
        // no-op
    }

    public void unfixColor()
    {
        // no-op
    }

    public ModelVAOData toData()
    {
        float[] v = positions.toArray();
        float[] n = normals.toArray();
        float[] t = tangents.toArray();
        float[] uv = texCoords.toArray();
        return new ModelVAOData(v, n, t, uv);
    }

    private static float[] toArray(List<Float> list)
    {
        float[] arr = new float[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = list.get(i);
        return arr;
    }
}
