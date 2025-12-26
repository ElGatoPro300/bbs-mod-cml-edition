package mchorse.bbs_mod.client;

import mchorse.bbs_mod.BBSMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Optional;

public class BBSShaders
{
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
        setup(MinecraftClient.getInstance().getResourceManager());
    }

    public static void setup(ResourceManager manager)
    {
        if (multiLink != null) multiLink.close();
        if (subtitles != null) subtitles.close();

        if (pickerPreview != null) pickerPreview.close();
        if (pickerBillboard != null) pickerBillboard.close();
        if (pickerBillboardNoShading != null) pickerBillboardNoShading.close();
        if (pickerParticles != null) pickerParticles.close();
        if (pickerModels != null) pickerModels.close();

        try
        {
            ResourceFactory factory = new ProxyResourceFactory(manager);
            
            multiLink = createShader(factory, "multilink", VertexFormats.POSITION_TEXTURE_COLOR);
            subtitles = createShader(factory, "subtitles", VertexFormats.POSITION_TEXTURE_COLOR);

            pickerPreview = createShader(factory, "picker_preview", VertexFormats.POSITION_TEXTURE_COLOR);
            pickerBillboard = createShader(factory, "picker_billboard", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            pickerBillboardNoShading = createShader(factory, "picker_billboard_no_shading", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
            pickerParticles = createShader(factory, "picker_particles", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
            pickerModels = createShader(factory, "picker_models", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static ShaderProgram createShader(ResourceFactory factory, String name, VertexFormat format)
    {
        try
        {
            for (Constructor<?> c : ShaderProgram.class.getDeclaredConstructors())
            {
                c.setAccessible(true);
                Class<?>[] types = c.getParameterTypes();
                if (types.length == 3 && ResourceFactory.class.isAssignableFrom(types[0]) && types[1] == String.class && VertexFormat.class.isAssignableFrom(types[2]))
                {
                    return (ShaderProgram) c.newInstance(factory, name, format);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    public static ShaderProgram getModel()
    {
        // Usar programa vanilla para VAO en modo normal; no cargar shader "model" propio
        return null; // MinecraftClient.getInstance().gameRenderer.getRenderTypeEntityTranslucentCullProgram();
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

    private static class ProxyResourceFactory implements ResourceFactory
    {
        private ResourceManager manager;

        public ProxyResourceFactory(ResourceManager manager)
        {
            this.manager = manager;
        }

        @Override
        public Optional<Resource> getResource(Identifier id)
        {
            if (id.getPath().contains("/core/"))
            {
                return this.manager.getResource(Identifier.of(BBSMod.MOD_ID, id.getPath()));
            }

            return this.manager.getResource(id);
        }
    }
}
