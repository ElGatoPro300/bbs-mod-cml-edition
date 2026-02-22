package elgatopro300.bbs_cml.cubic.render.vao;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
// import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class ModelVAORenderer
{
    public static void render(ShaderProgram shader, IModelVAO modelVAO, MatrixStack stack, Matrix4f projectionMatrix, float r, float g, float b, float a, int light, int overlay)
    {
        int currentVAO = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int currentElementArrayBuffer = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);

        setupUniforms(stack, shader, r, g, b, a);

        // shader.bind();

        // int textureID = RenderSystem.getShaderTexture(0);
        // RenderSystem.activeTexture(GL30.GL_TEXTURE0);
        // RenderSystem.bindTexture(textureID);

        modelVAO.render(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, stack, projectionMatrix, r, g, b, a, light, overlay);
        // shader.unbind();

        GL30.glBindVertexArray(currentVAO);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, currentElementArrayBuffer);
    }

    public static void setupUniforms(MatrixStack stack, ShaderProgram shader, float r, float g, float b, float a)
    {
        if (shader == null)
        {
            return;
        }

        // if (shader.projectionMat != null)
        {
            // shader.projectionMat.set(projectionMatrix);
        }

        // if (shader.modelViewMat != null)
        {
            // shader.modelViewMat.set(new Matrix4f(new Matrix4f()));
        }

        GlUniform normalUniform = shader.getUniform("NormalMat");

        if (normalUniform != null)
        {
            // normalUniform.set(stack.peek().getNormalMatrix());
        }

        // if (shader.gameTime != null)
        {
            // shader.gameTime.set((float) (System.currentTimeMillis() / 1000.0));
        }

        // if (shader.textureMat != null)
        {
            // shader.textureMat.set(RenderSystem.getTextureMatrix());
        }

        // RenderSystem.setupShaderLights(shader);
    }
}

