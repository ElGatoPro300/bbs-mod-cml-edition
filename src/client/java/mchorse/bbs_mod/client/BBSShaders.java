package mchorse.bbs_mod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import java.io.IOException;

public class BBSShaders
{
    private static ShaderProgram model;
    private static ShaderProgram multilink;
    private static ShaderProgram subtitles;
    private static ShaderProgram pickerPreview;
    private static ShaderProgram pickerBillboard;
    private static ShaderProgram pickerBillboardNoShading;
    private static ShaderProgram pickerParticles;
    private static ShaderProgram pickerModels;

    /**
     * Preload custom BBS shader programs from assets (assets/bbs/shaders/core/*).
     * Must be called once during client startup.
     */
    public static void setup()
    {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            try
            {
                model = context.register(Identifier.of("bbs", "model"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
                multilink = context.register(Identifier.of("bbs", "multilink"), VertexFormats.POSITION_TEXTURE_COLOR);
                subtitles = context.register(Identifier.of("bbs", "subtitles"), VertexFormats.POSITION_TEXTURE_COLOR);
                pickerPreview = context.register(Identifier.of("bbs", "picker_preview"), VertexFormats.POSITION_TEXTURE_COLOR);
                pickerBillboard = context.register(Identifier.of("bbs", "picker_billboard"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
                pickerBillboardNoShading = context.register(Identifier.of("bbs", "picker_billboard_no_shading"), VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
                pickerParticles = context.register(Identifier.of("bbs", "picker_particles"), VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                pickerModels = context.register(Identifier.of("bbs", "picker_models"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error registrando shaders de BBS", e);
            }
        });
    }

    public static ShaderProgram getModel()
    {
        return model;
    }

    public static ShaderProgram getMultilinkProgram()
    {
        return multilink;
    }

    public static ShaderProgram getSubtitlesProgram()
    {
        return subtitles;
    }

    public static ShaderProgram getPickerPreviewProgram()
    {
        return pickerPreview;
    }

    public static ShaderProgram getPickerBillboardProgram()
    {
        return pickerBillboard;
    }

    public static ShaderProgram getPickerBillboardNoShadingProgram()
    {
        return pickerBillboardNoShading;
    }

    public static ShaderProgram getPickerParticlesProgram()
    {
        return pickerParticles;
    }

    public static ShaderProgram getPickerModelsProgram()
    {
        return pickerModels;
    }
}
