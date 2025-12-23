package mchorse.bbs_mod.cubic.render.vao;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class ModelVAORenderer
{
    public static void render(Runnable shaderSetup, ShaderProgram contextProgram, IModelVAO modelVAO, MatrixStack stack, float r, float g, float b, float a, int light, int overlay)
    {
        int currentVAO = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int currentElementArrayBuffer = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);

        shaderSetup.run();

        Matrix4f originalModelView = null;

        if (contextProgram != null)
        {
            setupUniforms(stack, contextProgram);
        }
        else
        {
            originalModelView = new Matrix4f(RenderSystem.getModelViewMatrix());
            
            RenderSystem.getModelViewMatrix().set(stack.peek().getPositionMatrix());
            
            ShaderProgram shader = RenderSystem.getShader();
            if (shader != null)
            {
                if (shader.modelViewMat != null) shader.modelViewMat.set(RenderSystem.getModelViewMatrix());
                if (shader.projectionMat != null) shader.projectionMat.set(RenderSystem.getProjectionMatrix());
                shader.bind();
            }
        }

        modelVAO.render(null, r, g, b, a, light, overlay);

        if (contextProgram != null)
        {
            GlStateManager._glUseProgram(0);
        }
        else
        {
            GlStateManager._glUseProgram(0);

            if (originalModelView != null)
            {
                RenderSystem.getModelViewMatrix().set(originalModelView);
                
                ShaderProgram shader = RenderSystem.getShader();
                if (shader != null && shader.modelViewMat != null)
                {
                    shader.modelViewMat.set(originalModelView);
                    shader.bind();
                }
            }
        }

        GL30.glBindVertexArray(currentVAO);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, currentElementArrayBuffer);
    }

    public static void setupUniforms(MatrixStack stack, ShaderProgram shader)
    {
        for (int i = 0; i < 12; i++)
        {
            // shader.addSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
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

        if (shader.fogStart != null)
        {
            // shader.fogStart.set(RenderSystem.getShaderFogStart());
        }

        if (shader.fogEnd != null)
        {
            // shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.fogColor != null)
        {
            // shader.fogColor.set(RenderSystem.getShaderFogColor());
        }

        if (shader.fogShape != null)
        {
            // shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }

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