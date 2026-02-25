package elgatopro300.bbs_cml.client;

import com.mojang.blaze3d.systems.RenderSystem;

import elgatopro300.bbs_cml.BBSMod;
import net.minecraft.client.MinecraftClient;
// import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.shaders.ShaderType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BBSShaders
{
    public static final List<Runnable> LOADERS = new ArrayList<>();

    private static ShaderProgram model;
    private static ShaderProgram multiLink;
    private static ShaderProgram subtitles;

    private static ShaderProgram pickerPreview;
    private static ShaderProgram pickerBillboard;
    private static ShaderProgram pickerBillboardNoShading;
    private static ShaderProgram pickerParticles;
    private static ShaderProgram pickerModels;

    static
    {
        setup();
    }

    public static void setup()
    {
        if (model != null) model.close();
        if (subtitles != null) subtitles.close();
        if (subtitles != null) subtitles.close();

        if (pickerPreview != null) pickerPreview.close();
        if (pickerBillboard != null) pickerBillboard.close();
        if (pickerBillboardNoShading != null) pickerBillboardNoShading.close();
        if (pickerParticles != null) pickerParticles.close();
        if (pickerModels != null) pickerModels.close();

        ShaderLoader loader = MinecraftClient.getInstance().getShaderLoader();
        Defines defines = Defines.EMPTY;

        try
        {
            model = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/model"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            multiLink = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/multilink"), VertexFormats.POSITION_TEXTURE_COLOR);
            subtitles = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/subtitles"), VertexFormats.POSITION_TEXTURE_COLOR);

            pickerPreview = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/picker_preview"), VertexFormats.POSITION_TEXTURE_COLOR);
            pickerBillboard = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/picker_billboard"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            pickerBillboardNoShading = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/picker_billboard_no_shading"), VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
            pickerParticles = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/picker_particles"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
            pickerModels = createProgram(loader, Identifier.of(BBSMod.MOD_ID, "core/picker_models"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
        
            for (Runnable runnable : LOADERS)
            {
                runnable.run();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static ShaderProgram createProgram(ShaderLoader loader, Identifier id, VertexFormat format) throws Exception
    {
        /*
        String vertexSource = loader.getSource(id, ShaderType.VERTEX);
        String fragmentSource = loader.getSource(id, ShaderType.FRAGMENT);

        CompiledShader vertexShader = CompiledShader.compile(id, ShaderType.VERTEX, vertexSource);
        CompiledShader fragmentShader = CompiledShader.compile(id, ShaderType.FRAGMENT, fragmentSource);

        return ShaderProgram.create(vertexShader, fragmentShader, format, id.toString());
        */
        return null;
    }

    public static ShaderProgram getModel()
    {
        return model;
    }

    public static ShaderProgram getMultilinkProgram()
    {
        return multiLink;
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
