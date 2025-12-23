package mchorse.bbs_mod.client;

import mchorse.bbs_mod.BBSMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Optional;
import java.lang.reflect.Method;

public class BBSShaders
{
    private static Method getProgramMethod;

    static
    {
        setup();
    }

    public static void setup()
    {
        getProgramMethod = null;
    }

    private static ShaderProgram getProgram(String name)
    {
        try
        {
            if (getProgramMethod == null)
            {
                // Try to find the method on GameRenderer
                for (Method m : GameRenderer.class.getDeclaredMethods())
                {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == String.class && m.getReturnType() == ShaderProgram.class)
                    {
                        m.setAccessible(true);
                        getProgramMethod = m;
                        break;
                    }
                }
            }

            if (getProgramMethod != null)
            {
                return (ShaderProgram) getProgramMethod.invoke(MinecraftClient.getInstance().gameRenderer, name);
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
        return getProgram("multilink");
    }

    public static ShaderProgram getSubtitlesProgram()
    {
        return getProgram("subtitles");
    }

    public static ShaderProgram getPickerPreviewProgram()
    {
        return getProgram("picker_preview");
    }

    public static ShaderProgram getPickerBillboardProgram()
    {
        return getProgram("picker_billboard");
    }

    public static ShaderProgram getPickerBillboardNoShadingProgram()
    {
        return getProgram("picker_billboard_no_shading");
    }

    public static ShaderProgram getPickerParticlesProgram()
    {
        return getProgram("picker_particles");
    }

    public static ShaderProgram getPickerModelsProgram()
    {
        return getProgram("picker_models");
    }
}
