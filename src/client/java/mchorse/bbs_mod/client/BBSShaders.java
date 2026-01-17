package mchorse.bbs_mod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.BBSMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class BBSShaders
{
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

        try
        {
            ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
            ResourceFactory factory = new ProxyResourceFactory(manager);

            model = loadProgram(manager, factory, "model", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            multiLink = loadProgram(manager, factory, "multilink", VertexFormats.POSITION_TEXTURE_COLOR);
            subtitles = loadProgram(manager, factory, "subtitles", VertexFormats.POSITION_TEXTURE_COLOR);

            pickerPreview = loadProgram(manager, factory, "picker_preview", VertexFormats.POSITION_TEXTURE_COLOR);
            pickerBillboard = loadProgram(manager, factory, "picker_billboard", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            pickerBillboardNoShading = loadProgram(manager, factory, "picker_billboard_no_shading", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
            pickerParticles = loadProgram(manager, factory, "picker_particles", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
            pickerModels = loadProgram(manager, factory, "picker_models", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    private static ShaderProgram loadProgram(ResourceManager manager, ResourceFactory factory, String name, VertexFormat format) throws IOException
    {
        try
        {
            for (Method method : ShaderProgram.class.getDeclaredMethods())
            {
                if (!java.lang.reflect.Modifier.isStatic(method.getModifiers()))
                {
                    continue;
                }

                if (!ShaderProgram.class.isAssignableFrom(method.getReturnType()))
                {
                    continue;
                }

                Class<?>[] parameters = method.getParameterTypes();

                if (parameters.length == 3 && ResourceFactory.class.isAssignableFrom(parameters[0]) && parameters[1] == String.class && VertexFormat.class.isAssignableFrom(parameters[2]))
                {
                    method.setAccessible(true);
                    return (ShaderProgram) method.invoke(null, factory, name, format);
                }
            }

            for (java.lang.reflect.Constructor<?> constructor : ShaderProgram.class.getDeclaredConstructors())
            {
                Class<?>[] parameters = constructor.getParameterTypes();

                if (parameters.length == 3 && ResourceFactory.class.isAssignableFrom(parameters[0]) && parameters[1] == String.class && VertexFormat.class.isAssignableFrom(parameters[2]))
                {
                    constructor.setAccessible(true);
                    return (ShaderProgram) constructor.newInstance(factory, name, format);
                }
            }

            throw new IOException("Failed to find way to construct ShaderProgram for shader program: " + name);
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Failed to load shader program: " + name, e);
        }
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
