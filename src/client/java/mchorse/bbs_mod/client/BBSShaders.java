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

        // Inicializar de forma segura: si algún shader no existe, mantenerlo en null
        ResourceFactory factory = new ProxyResourceFactory(MinecraftClient.getInstance().getResourceManager());

        // El shader "model" de core puede no existir en 1.21.1; usar fallback en el getter
        model = null;

        try { multiLink = new ShaderProgram(factory, "multilink", VertexFormats.POSITION_TEXTURE_COLOR); } catch (Throwable t) { multiLink = null; }
        try { subtitles = new ShaderProgram(factory, "subtitles", VertexFormats.POSITION_TEXTURE_COLOR); } catch (Throwable t) { subtitles = null; }

        try { pickerPreview = new ShaderProgram(factory, "picker_preview", VertexFormats.POSITION_TEXTURE_COLOR); } catch (Throwable t) { pickerPreview = null; }
        try { pickerBillboard = new ShaderProgram(factory, "picker_billboard", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL); } catch (Throwable t) { pickerBillboard = null; }
        try { pickerBillboardNoShading = new ShaderProgram(factory, "picker_billboard_no_shading", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR); } catch (Throwable t) { pickerBillboardNoShading = null; }
        try { pickerParticles = new ShaderProgram(factory, "picker_particles", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT); } catch (Throwable t) { pickerParticles = null; }
        try { pickerModels = new ShaderProgram(factory, "picker_models", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL); } catch (Throwable t) { pickerModels = null; }
    }

    public static ShaderProgram getModel()
    {
        // Fallback seguro para formatos con overlay/light/normal
        return model != null ? model : GameRenderer.getRenderTypeEntityTranslucentCullProgram();
    }

    public static ShaderProgram getMultilinkProgram()
    {
        // Fallback a shader estándar de textura+color para UI
        return multiLink != null ? multiLink : GameRenderer.getPositionTexColorProgram();
    }

    public static ShaderProgram getSubtitlesProgram()
    {
        return subtitles != null ? subtitles : GameRenderer.getPositionTexColorProgram();
    }

    public static ShaderProgram getPickerPreviewProgram()
    {
        return pickerPreview != null ? pickerPreview : GameRenderer.getPositionTexColorProgram();
    }

    public static ShaderProgram getPickerBillboardProgram()
    {
        // Fallback a programa con formato extendido
        return pickerBillboard != null ? pickerBillboard : GameRenderer.getRenderTypeEntityTranslucentCullProgram();
    }

    public static ShaderProgram getPickerBillboardNoShadingProgram()
    {
        // Fallback a textura+light+color si está disponible; usar programa básico si no
        return pickerBillboardNoShading != null ? pickerBillboardNoShading : GameRenderer.getPositionTexColorProgram();
    }

    public static ShaderProgram getPickerParticlesProgram()
    {
        return pickerParticles != null ? pickerParticles : GameRenderer.getPositionTexColorProgram();
    }

    public static ShaderProgram getPickerModelsProgram()
    {
        return pickerModels != null ? pickerModels : GameRenderer.getRenderTypeEntityTranslucentCullProgram();
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
            // Si el ResourceManager aún no está listo, evitar NPE y no remapear
            if (this.manager == null)
            {
                return Optional.empty();
            }

            // No remapear recursos de vanilla (minecraft) ni rutas core.
            // Mantener el comportamiento por defecto para que los shaders estándar se carguen correctamente.
            return this.manager.getResource(id);
        }
    }
}
