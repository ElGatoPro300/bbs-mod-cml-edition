package elgatopro300.bbs_cml.cubic.render;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.cubic.ModelInstance;
import elgatopro300.bbs_cml.cubic.data.model.Model;
import elgatopro300.bbs_cml.cubic.data.model.ModelGroup;
import elgatopro300.bbs_cml.cubic.render.vao.ModelVAO;
import elgatopro300.bbs_cml.cubic.render.vao.ModelVAORenderer;
import elgatopro300.bbs_cml.obj.shapes.ShapeKeys;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.framework.elements.utils.StencilMap;
import elgatopro300.bbs_cml.utils.MathUtils;
import elgatopro300.bbs_cml.utils.interps.Lerps;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

public class CubicVAORenderer extends CubicCubeRenderer
{
    private ShaderProgram program;
    private ModelInstance model;
    private Link defaultTexture;

    public CubicVAORenderer(ShaderProgram program, ModelInstance model, int light, int overlay, StencilMap stencilMap, ShapeKeys shapeKeys, Link defaultTexture)
    {
        super(light, overlay, stencilMap, shapeKeys);

        this.program = program;
        this.model = model;
        this.defaultTexture = defaultTexture;
    }

    @Override
    public boolean renderGroup(BufferBuilder builder, MatrixStack stack, ModelGroup group, Model model)
    {
        ModelVAO modelVAO = this.model.getVaos().get(group);

        if (modelVAO != null && group.visible)
        {
            float a = this.a * group.color.a;

            if (a <= 0F)
            {
                return false;
            }

            final float TRANSP_EPS = 0.999f;
            if (this.transparentPass)
            {
                if (a >= TRANSP_EPS) return false;
            }
            else
            {
                if (a < TRANSP_EPS) return false;
            }

            if (group.textureOverride != null)
            {
                BBSModClient.getTextures().bindTexture(group.textureOverride);
            }
            else if (this.defaultTexture != null)
            {
                BBSModClient.getTextures().bindTexture(this.defaultTexture);
            }
            else
            {
                BBSModClient.getTextures().bindTexture(this.model.texture);
            }

            float r = this.r * group.color.r;
            float g = this.g * group.color.g;
            float b = this.b * group.color.b;
            int light = this.light;

            if (this.stencilMap != null)
            {
                light = this.stencilMap.increment ? group.index : 0;
            }
            else
            {
                int u = (int) Lerps.lerp(light & '\uffff', LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, MathUtils.clamp(group.lighting, 0F, 1F));
                int v = light >> 16 & '\uffff';

                light = u | v << 16;
            }

            ModelVAORenderer.render(this.program, modelVAO, stack, r, g, b, a, light, this.overlay);
        }

        return false;
    }
}
