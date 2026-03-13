package mchorse.bbs_mod.cubic.render.vao;

import mchorse.bbs_mod.bobj.BOBJArmature;
import mchorse.bbs_mod.bobj.BOBJBone;
import mchorse.bbs_mod.bobj.BOBJLoader;
import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.ui.framework.elements.utils.StencilMap;
import mchorse.bbs_mod.utils.joml.Matrices;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class BOBJModelVAO
{
    public BOBJLoader.CompiledData data;
    public BOBJArmature armature;

    private int vao;
    private int count;

    /* GL buffers */
    public int vertexBuffer;
    public int normalBuffer;
    public int lightBuffer;
    public int texCoordBuffer;
    public int tangentBuffer;
    public int midTextureBuffer;

    public int opaqueIndexBuffer;
    public int translucentIndexBuffer;

    private float[] tmpVertices;
    private float[] tmpNormals;
    private int[] tmpLight;
    private float[] tmpTangents;
    private float[] tmpColors;
    private int[] tmpBones;

    public int opaqueIndexCount;
    public int translucentIndexCount;
    public int[] cachedOpaqueIdx;
    public int[] cachedTransIdx;
    public boolean hasSemiTransparency;

    public BOBJModelVAO(BOBJLoader.CompiledData data, BOBJArmature armature)
    {
        this.data = data;
        this.armature = armature;

        if (this.data != null && this.data.posData != null && this.data.normData != null)
        {
            this.initBuffers();
        }
    }

    /**
     * Initiate buffers. This method is responsible for allocating 
     * buffers for the data to be passed to VBOs and also generating the 
     * VBOs themselves. 
     */
    private void initBuffers()
    {
        if (this.data == null || this.data.posData == null || this.data.normData == null)
        {
            return;
        }

        this.vao = GL30.glGenVertexArrays();

        GL30.glBindVertexArray(this.vao);

        this.vertexBuffer = GL30.glGenBuffers();
        this.normalBuffer = GL30.glGenBuffers();
        this.lightBuffer = GL30.glGenBuffers();
        this.texCoordBuffer = GL30.glGenBuffers();
        this.tangentBuffer = GL30.glGenBuffers();
        this.midTextureBuffer = GL30.glGenBuffers();

        this.opaqueIndexBuffer = GL30.glGenBuffers();
        this.translucentIndexBuffer = GL30.glGenBuffers();

        this.count = this.data.normData.length / 3;
        this.tmpVertices = new float[this.data.posData.length];
        this.tmpNormals = new float[this.data.normData.length];
        this.tmpLight = new int[this.count * 2];
        this.tmpTangents = new float[this.count * 4];
        this.tmpColors = new float[this.count * 4];
        this.tmpBones = new int[this.count];

        if (this.data.indexData != null)
        {
            this.opaqueIndexCount = this.data.indexData.length;
            this.cachedOpaqueIdx = this.data.indexData;

            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.opaqueIndexBuffer);
            GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, this.data.indexData, GL30.GL_STATIC_DRAW);
        }
        else
        {
            this.opaqueIndexCount = 0;
            this.cachedOpaqueIdx = new int[0];
        }

        this.translucentIndexCount = 0;
        this.cachedTransIdx = new int[0];
        this.hasSemiTransparency = false;

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vertexBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, this.data.posData, GL30.GL_DYNAMIC_DRAW);
        GL30.glVertexAttribPointer(Attributes.POSITION, 3, GL30.GL_FLOAT, false, 0, 0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.normalBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, this.data.normData, GL30.GL_DYNAMIC_DRAW);
        GL30.glVertexAttribPointer(Attributes.NORMAL, 3, GL30.GL_FLOAT, false, 0, 0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.lightBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, this.tmpLight, GL30.GL_DYNAMIC_DRAW);
        GL30.glVertexAttribIPointer(Attributes.LIGHTMAP_UV, 2, GL30.GL_INT, 0, 0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.texCoordBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, this.data.texData, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(Attributes.TEXTURE_UV, 2, GL30.GL_FLOAT, false, 0, 0);
        GL30.glVertexAttribPointer(Attributes.MID_TEXTURE_UV, 2, GL30.GL_FLOAT, false, 0, 0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.tangentBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, this.tmpTangents, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(Attributes.TANGENTS, 4, GL30.GL_FLOAT, false, 0, 0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    /**
     * Clean up resources which were used by this  
     */
    public void delete()
    {
        if (this.vao != 0)
        {
            GL30.glDeleteVertexArrays(this.vao);
        }

        if (this.vertexBuffer != 0) GL15.glDeleteBuffers(this.vertexBuffer);
        if (this.normalBuffer != 0) GL15.glDeleteBuffers(this.normalBuffer);
        if (this.lightBuffer != 0) GL15.glDeleteBuffers(this.lightBuffer);
        if (this.texCoordBuffer != 0) GL15.glDeleteBuffers(this.texCoordBuffer);
        if (this.tangentBuffer != 0) GL15.glDeleteBuffers(this.tangentBuffer);
        if (this.midTextureBuffer != 0) GL15.glDeleteBuffers(this.midTextureBuffer);
    }

    /**
     * Update this mesh. This method is responsible for applying 
     * matrix transformations to vertices and normals according to its 
     * bone owners and these bone influences.
     */
    public void updateMesh(StencilMap stencilMap)
    {
        if (this.data == null || this.armature == null || this.data.posData == null || this.data.normData == null || this.armature.matrices == null || this.vertexBuffer == 0)
        {
            return;
        }

        Vector4f sum = new Vector4f();
        Vector4f result = new Vector4f(0F, 0F, 0F, 0F);
        Vector3f sumNormal = new Vector3f();
        Vector3f resultNormal = new Vector3f();

        float[] oldVertices = this.data.posData;
        float[] newVertices = this.tmpVertices;
        float[] oldNormals = this.data.normData;
        float[] newNormals = this.tmpNormals;

        Matrix4f[] matrices = this.armature.matrices;

        for (int i = 0, c = this.count; i < c; i++)
        {
            int count = 0;
            float maxWeight = -1;
            int lightBone = -1;

            if (this.data.weightData != null && this.data.boneIndexData != null)
            {
                for (int w = 0; w < 4; w++)
                {
                    float weight = this.data.weightData[i * 4 + w];

                    if (weight > 0)
                    {
                        int index = this.data.boneIndexData[i * 4 + w];

                        if (index >= 0 && index < matrices.length)
                        {
                            sum.set(oldVertices[i * 3], oldVertices[i * 3 + 1], oldVertices[i * 3 + 2], 1F);
                            matrices[index].transform(sum);
                            result.add(sum.mul(weight));

                            sumNormal.set(oldNormals[i * 3], oldNormals[i * 3 + 1], oldNormals[i * 3 + 2]);
                            Matrices.TEMP_3F.set(matrices[index]).transform(sumNormal);
                            resultNormal.add(sumNormal.mul(weight));

                            count++;

                            if (weight > maxWeight)
                            {
                                lightBone = index;
                                maxWeight = weight;
                            }
                        }
                    }
                }
            }

            if (count == 0)
            {
                result.set(oldVertices[i * 3], oldVertices[i * 3 + 1], oldVertices[i * 3 + 2], 1F);
                resultNormal.set(oldNormals[i * 3], oldNormals[i * 3 + 1], oldNormals[i * 3 + 2]);
            }

            result.x /= result.w;
            result.y /= result.w;
            result.z /= result.w;

            newVertices[i * 3] = result.x;
            newVertices[i * 3 + 1] = result.y;
            newVertices[i * 3 + 2] = result.z;

            newNormals[i * 3] = resultNormal.x;
            newNormals[i * 3 + 1] = resultNormal.y;
            newNormals[i * 3 + 2] = resultNormal.z;

            result.set(0F, 0F, 0F, 0F);
            resultNormal.set(0F, 0F, 0F);

            boolean allowBone = true;
            if (stencilMap != null && stencilMap.allowedBones != null && lightBone >= 0 && lightBone < this.armature.orderedBones.size())
            { 
                String boneName = this.armature.orderedBones.get(lightBone).name;
                allowBone = stencilMap.allowedBones.contains(boneName);
            }

            if (stencilMap != null)
            {
                this.tmpLight[i * 2] = Math.max(0, stencilMap.increment ? (allowBone ? lightBone : 0) : 0);
                this.tmpLight[i * 2 + 1] = 0;
            }

            if (lightBone != -1 && lightBone < this.armature.orderedBones.size())
            {
                mchorse.bbs_mod.utils.colors.Color color = this.armature.orderedBones.get(lightBone).color;

                this.tmpColors[i * 4] = color.r;
                this.tmpColors[i * 4 + 1] = color.g;
                this.tmpColors[i * 4 + 2] = color.b;
                this.tmpColors[i * 4 + 3] = color.a;
            }
            else
            {
                this.tmpColors[i * 4] = 1F;
                this.tmpColors[i * 4 + 1] = 1F;
                this.tmpColors[i * 4 + 2] = 1F;
                this.tmpColors[i * 4 + 3] = 1F;
            }

            if (!allowBone)
            {
                this.tmpColors[i * 4 + 3] = 0F;
            }

            this.tmpBones[i] = lightBone;
        }

        this.processData(newVertices, newNormals);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, newVertices, GL15.GL_DYNAMIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.normalBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, newNormals, GL15.GL_DYNAMIC_DRAW);

        if (BBSRendering.isIrisShadersEnabled())
        {
            BBSRendering.calculateTangents(this.tmpTangents, newVertices, newNormals, this.data.texData);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.tangentBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.tmpTangents, GL15.GL_DYNAMIC_DRAW);
        }

        if (stencilMap != null)
        {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.lightBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.tmpLight, GL15.GL_DYNAMIC_DRAW);
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    protected void processData(float[] newVertices, float[] newNormals)
    {}

    public void render(ShaderProgram shader, MatrixStack stack, float r, float g, float b, float a, StencilMap stencilMap, int light, int overlay)
    {
        if (shader == null || this.vao == 0)
        {
            return;
        }

        boolean hasShaders = BBSRendering.isIrisShadersEnabled();

        GL30.glVertexAttrib4f(Attributes.COLOR, r, g, b, a);
        GL30.glVertexAttribI2i(Attributes.OVERLAY_UV, overlay & '\uffff', overlay >> 16 & '\uffff');
        GL30.glVertexAttribI2i(Attributes.LIGHTMAP_UV, light & '\uffff', light >> 16 & '\uffff');

        int currentVAO = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int currentElementArrayBuffer = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);

        ModelVAORenderer.setupUniforms(stack, shader);

        shader.bind();

        GL30.glBindVertexArray(this.vao);

        GL30.glEnableVertexAttribArray(Attributes.POSITION);
        GL30.glEnableVertexAttribArray(Attributes.TEXTURE_UV);
        GL30.glEnableVertexAttribArray(Attributes.NORMAL);

        if (stencilMap != null) GL30.glEnableVertexAttribArray(Attributes.LIGHTMAP_UV);
        if (hasShaders) GL30.glEnableVertexAttribArray(Attributes.TANGENTS);
        if (hasShaders) GL30.glEnableVertexAttribArray(Attributes.MID_TEXTURE_UV);

        boolean globalTranslucent = a < 1.0F;
        boolean semiTransparent = globalTranslucent || this.hasSemiTransparency;

        com.mojang.blaze3d.systems.RenderSystem.enableCull();

        /* Opaque pass first (only if global alpha is 1) */
        if (!globalTranslucent)
        {
            GL11.glCullFace(GL11.GL_BACK);

            if (this.opaqueIndexCount > 0)
            {
                boolean hasBoneTextures = false;

                for (BOBJBone bone : this.armature.orderedBones)
                {
                    if (bone.texture != null)
                    {
                        hasBoneTextures = true;
                        break;
                    }
                }

                if (!hasBoneTextures)
                {
                    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.opaqueIndexBuffer);
                    GL30.glDrawElements(GL30.GL_TRIANGLES, this.opaqueIndexCount, GL30.GL_UNSIGNED_INT, 0L);
                }
                else
                {
                    int triCount = this.opaqueIndexCount / 3;
                    int[] baseIdx = this.cachedOpaqueIdx;
                    int[] indices = new int[this.opaqueIndexCount];
                    int prevTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

                    for (BOBJBone bone : this.armature.orderedBones)
                    {
                        if (bone.texture == null)
                        {
                            continue;
                        }

                        int boneIndex = bone.index;
                        int countIdx = 0;

                        for (int t = 0; t < triCount; t++)
                        {
                            int i0 = baseIdx[t * 3];
                            int i1 = baseIdx[t * 3 + 1];
                            int i2 = baseIdx[t * 3 + 2];
                            int triBone = this.getTriangleBone(i0, i1, i2);

                            if (triBone == boneIndex)
                            {
                                indices[countIdx++] = i0;
                                indices[countIdx++] = i1;
                                indices[countIdx++] = i2;
                            }
                        }

                        if (countIdx > 0)
                        {
                            BBSModClient.getTextures().bindTexture(bone.texture);
                            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.opaqueIndexBuffer);
                            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, java.util.Arrays.copyOf(indices, countIdx), GL15.GL_DYNAMIC_DRAW);
                            GL30.glDrawElements(GL30.GL_TRIANGLES, countIdx, GL30.GL_UNSIGNED_INT, 0L);
                        }
                    }

                    int defaultCount = 0;

                    for (int t = 0; t < triCount; t++)
                    {
                        int i0 = baseIdx[t * 3];
                        int i1 = baseIdx[t * 3 + 1];
                        int i2 = baseIdx[t * 3 + 2];
                        int triBone = this.getTriangleBone(i0, i1, i2);
                        BOBJBone bone = triBone >= 0 && triBone < this.armature.orderedBones.size() ? this.armature.orderedBones.get(triBone) : null;

                        if (bone == null || bone.texture == null)
                        {
                            indices[defaultCount++] = i0;
                            indices[defaultCount++] = i1;
                            indices[defaultCount++] = i2;
                        }
                    }

                    if (defaultCount > 0)
                    {
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexture);
                        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.opaqueIndexBuffer);
                        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, java.util.Arrays.copyOf(indices, defaultCount), GL15.GL_DYNAMIC_DRAW);
                        GL30.glDrawElements(GL30.GL_TRIANGLES, defaultCount, GL30.GL_UNSIGNED_INT, 0L);
                    }

                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexture);
                }
            }
            else
            {
                GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, this.count);
            }
        }

        /* Translucent pass */
        if (semiTransparent)
        {
            com.mojang.blaze3d.systems.RenderSystem.depthMask(false);

            /* Render opaque part as translucent if global alpha < 1 */
            if (globalTranslucent)
            {
                if (this.opaqueIndexCount > 0)
                {
                    GL11.glCullFace(GL11.GL_FRONT);
                    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.opaqueIndexBuffer);
                    GL30.glDrawElements(GL30.GL_TRIANGLES, this.opaqueIndexCount, GL30.GL_UNSIGNED_INT, 0L);

                    GL11.glCullFace(GL11.GL_BACK);
                    GL30.glDrawElements(GL30.GL_TRIANGLES, this.opaqueIndexCount, GL30.GL_UNSIGNED_INT, 0L);
                }
                else
                {
                    GL11.glCullFace(GL11.GL_FRONT);
                    GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, this.count);

                    GL11.glCullFace(GL11.GL_BACK);
                    GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, this.count);
                }
            }

            if (this.translucentIndexCount > 0)
            {
                /* Sort translucent triangles back-to-front using current model-view */
                org.joml.Matrix4f mv = new org.joml.Matrix4f(com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix()).mul(stack.peek().getPositionMatrix());
                int triCount = this.translucentIndexCount / 3;
                Integer[] order = new Integer[triCount];
                for (int i = 0; i < triCount; i++) order[i] = i;

                org.joml.Vector4f vtx = new org.joml.Vector4f();
                java.util.Arrays.sort(order, (aIdx, bIdx) -> {
                    int a0 = aIdx * 3;
                    int ia0 = this.getTransIndex(a0);
                    int ia1 = this.getTransIndex(a0 + 1);
                    int ia2 = this.getTransIndex(a0 + 2);
                    float az = centroidZ(ia0, ia1, ia2, this.tmpVertices, mv, vtx);

                    int b0 = bIdx * 3;
                    int ib0 = this.getTransIndex(b0);
                    int ib1 = this.getTransIndex(b0 + 1);
                    int ib2 = this.getTransIndex(b0 + 2);
                    float bz = centroidZ(ib0, ib1, ib2, this.tmpVertices, mv, vtx);

                    return Float.compare(az, bz); /* back-to-front (Z more positive is farther) */
                });

                int[] sortedTrans = new int[this.translucentIndexCount];
                int p = 0;
                for (int i = 0; i < triCount; i++)
                {
                    int base = order[i] * 3;
                    sortedTrans[p++] = this.getTransIndex(base);
                    sortedTrans[p++] = this.getTransIndex(base + 1);
                    sortedTrans[p++] = this.getTransIndex(base + 2);
                }

                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.translucentIndexBuffer);
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, sortedTrans, GL15.GL_DYNAMIC_DRAW);

                GL11.glCullFace(GL11.GL_FRONT);
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.translucentIndexBuffer);
                GL30.glDrawElements(GL30.GL_TRIANGLES, this.translucentIndexCount, GL30.GL_UNSIGNED_INT, 0L);

                GL11.glCullFace(GL11.GL_BACK);
                GL30.glDrawElements(GL30.GL_TRIANGLES, this.translucentIndexCount, GL30.GL_UNSIGNED_INT, 0L);
            }

            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
        }

        GL30.glDisableVertexAttribArray(Attributes.POSITION);
        GL30.glDisableVertexAttribArray(Attributes.TEXTURE_UV);
        GL30.glDisableVertexAttribArray(Attributes.NORMAL);

        if (stencilMap != null) GL30.glDisableVertexAttribArray(Attributes.LIGHTMAP_UV);
        if (hasShaders) GL30.glDisableVertexAttribArray(Attributes.TANGENTS);
        if (hasShaders) GL30.glDisableVertexAttribArray(Attributes.MID_TEXTURE_UV);

        shader.unbind();

        GL30.glBindVertexArray(currentVAO);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, currentElementArrayBuffer);
    }

    private int getTransIndex(int i)
    {
        return (this.cachedTransIdx != null && i >= 0 && i < this.cachedTransIdx.length) ? this.cachedTransIdx[i] : i;
    }

    private static float centroidZ(int i0, int i1, int i2, float[] verts, org.joml.Matrix4f mv, org.joml.Vector4f tmp)
    {
        float z = 0f;
        tmp.set(verts[i0 * 3], verts[i0 * 3 + 1], verts[i0 * 3 + 2], 1f).mul(mv);
        z += tmp.z;
        tmp.set(verts[i1 * 3], verts[i1 * 3 + 1], verts[i1 * 3 + 2], 1f).mul(mv);
        z += tmp.z;
        tmp.set(verts[i2 * 3], verts[i2 * 3 + 1], verts[i2 * 3 + 2], 1f).mul(mv);
        z += tmp.z;
        return z / 3f;
    }

    private int getTriangleBone(int v0, int v1, int v2)
    {
        int b0 = v0 >= 0 && v0 < this.tmpBones.length ? this.tmpBones[v0] : -1;
        int b1 = v1 >= 0 && v1 < this.tmpBones.length ? this.tmpBones[v1] : -1;
        int b2 = v2 >= 0 && v2 < this.tmpBones.length ? this.tmpBones[v2] : -1;

        if (b0 == b1 || b0 == b2)
        {
            return b0;
        }

        if (b1 == b2)
        {
            return b1;
        }

        if (b0 != -1)
        {
            return b0;
        }

        if (b1 != -1)
        {
            return b1;
        }

        return b2;
    }
}
