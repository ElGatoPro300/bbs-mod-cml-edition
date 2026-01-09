package net.minecraft.client.render;

import net.minecraft.util.Identifier;

public class BBSModRenderLayerHelper
{
    public static RenderLayer createParticleLayer(Identifier texture)
    {
        RenderLayer.MultiPhaseParameters params = RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.ENTITY_TRANSLUCENT_PROGRAM)
            .texture(new RenderPhase.Texture(texture, false, false))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .cull(RenderPhase.DISABLE_CULLING)
            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .build(false);

        return RenderLayer.of("bbs_particle",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.TRIANGLES,
            256,
            true,
            true,
            params
        );
    }
}