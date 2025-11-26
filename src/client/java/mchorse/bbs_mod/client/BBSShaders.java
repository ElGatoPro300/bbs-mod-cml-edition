package mchorse.bbs_mod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        try
        {
            Class<?> callbackClass = Class.forName("net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback");
            Field eventField = callbackClass.getField("EVENT");
            Object event = eventField.get(null);

            InvocationHandler handler = (proxy, method, args) -> {
                Object context = args[0];
                try
                {
                    Method register = context.getClass().getMethod("register", Identifier.class, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL.getClass());
                }
                catch (NoSuchMethodException ex)
                {
                    // Fallback: try generic signature (Identifier, Object)
                }

                try
                {
                    Method registerMethod = null;
                    for (Method m : context.getClass().getMethods())
                    {
                        if (m.getName().equals("register") && m.getParameterCount() == 2 && Identifier.class.isAssignableFrom(m.getParameterTypes()[0]))
                        {
                            registerMethod = m;
                            break;
                        }
                    }

                    if (registerMethod == null)
                    {
                        throw new NoSuchMethodException("No se encontró método register en RegistrationContext");
                    }

                    model = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "model"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
                    multilink = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "multilink"), VertexFormats.POSITION_TEXTURE_COLOR);
                    subtitles = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "subtitles"), VertexFormats.POSITION_TEXTURE_COLOR);
                    pickerPreview = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "picker_preview"), VertexFormats.POSITION_TEXTURE_COLOR);
                    pickerBillboard = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "picker_billboard"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
                    pickerBillboardNoShading = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "picker_billboard_no_shading"), VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
                    pickerParticles = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "picker_particles"), VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                    pickerModels = (ShaderProgram) registerMethod.invoke(context, Identifier.of("bbs", "picker_models"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Error registrando shaders de BBS via reflexión", e);
                }

                return null;
            };

            Object proxy = Proxy.newProxyInstance(
                BBSShaders.class.getClassLoader(),
                new Class<?>[] { callbackClass },
                handler
            );

            Method registerEvent = event.getClass().getMethod("register", Object.class);
            registerEvent.invoke(event, proxy);
        }
        catch (ClassNotFoundException e)
        {
            // Desde 1.21.2+, Fabric eliminó CoreShaderRegistrationCallback. Los core shaders de mods se cargan
            // automáticamente por vanilla desde assets/<modid>/shaders/core, sin necesidad de este callback.
            // Aquí no hacemos nada, y los shaders deberán resolverse cuando se usen.
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Error inicializando registro de shaders", t);
        }
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
