package mchorse.bbs_mod.cubic.render.vao;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class ModelVAORenderer
{
    public static void render(ShaderProgram shader, IModelVAO modelVAO, MatrixStack stack, float r, float g, float b, float a, int light, int overlay)
    {
        // Guard against null shader: try a safe fallback compatible with VAO format
        if (shader == null)
        {
            RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_ENTITY_TRANSLUCENT);
            ShaderProgram fallback = RenderSystem.getShader();

            if (fallback == null)
            {
                // No compatible program available; skip rendering to avoid crashing
                return;
            }

            shader = fallback;
        }

        int currentVAO = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int currentElementArrayBuffer = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);

        setupUniforms(stack, shader);

        shader.bind();

        int textureID = RenderSystem.getShaderTexture(0);
        GlStateManager._activeTexture(GL30.GL_TEXTURE0);
        GlStateManager._bindTexture(textureID);

        modelVAO.render(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, r, g, b, a, light, overlay);
        shader.unbind();

        GL30.glBindVertexArray(currentVAO);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, currentElementArrayBuffer);
    }

    public static void setupUniforms(MatrixStack stack, ShaderProgram shader)
    {
        /* Ensure the shader knows to use texture unit 0 for Sampler0 */
        GlUniform sampler0 = shader.getUniform("Sampler0");
        if (sampler0 != null)
        {
            sampler0.set(0);
        }

        if (shader.projectionMat != null)
        {
            shader.projectionMat.set(RenderSystem.getProjectionMatrix());
        }

        if (shader.modelViewMat != null)
        {
            shader.modelViewMat.set(new Matrix4f(RenderSystem.getModelViewMatrix()).mul(stack.peek().getPositionMatrix()));
        }

        /* NormalMat is present by default in Iris' shaders, but when there is no Iris,
         * the BBS mod's model.json shader is being used instead that provides NormalMat
         * uniform.
         */
        GlUniform normalUniform = shader.getUniform("NormalMat");

        if (normalUniform != null)
        {
            normalUniform.set(stack.peek().getNormalMatrix());
        }

        // 1.21.1: viewRotationMat was removed; try optional uniform by name
        GlUniform viewRot = shader.getUniform("IViewRotMat");
        if (viewRot != null)
        {
            // Use identity; billboard renderers adjust matrices separately
            viewRot.set(new org.joml.Matrix4f().identity());
        }

        /* TODO: 1.21.4 update - find replacement for RenderSystem.getShaderFogStart()
        if (shader.fogStart != null)
        {
            shader.fogStart.set(RenderSystem.getShaderFogStart());
        }

        if (shader.fogEnd != null)
        {
            shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.fogColor != null)
        {
            shader.fogColor.set(RenderSystem.getShaderFogColor());
        }

        if (shader.fogShape != null)
        {
            shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }
        */

        if (shader.colorModulator != null)
        {
            shader.colorModulator.set(1F, 1F, 1F, 1F);
        }

        if (shader.gameTime != null)
        {
            shader.gameTime.set(RenderSystem.getShaderGameTime());
        }

        if (shader.textureMat != null)
        {
            shader.textureMat.set(RenderSystem.getTextureMatrix());
        }

        RenderSystem.setupShaderLights(shader);
    }
}