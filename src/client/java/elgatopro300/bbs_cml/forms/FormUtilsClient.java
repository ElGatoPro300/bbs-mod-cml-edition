package elgatopro300.bbs_cml.forms;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import elgatopro300.bbs_cml.forms.forms.AnchorForm;
import elgatopro300.bbs_cml.forms.forms.BillboardForm;
import elgatopro300.bbs_cml.forms.forms.BlockForm;
import elgatopro300.bbs_cml.forms.forms.ExtrudedForm;
import elgatopro300.bbs_cml.forms.forms.FluidForm;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.forms.FramebufferForm;
import elgatopro300.bbs_cml.forms.forms.ItemForm;
import elgatopro300.bbs_cml.forms.forms.LabelForm;
import elgatopro300.bbs_cml.forms.forms.MobForm;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.forms.ParticleForm;
import elgatopro300.bbs_cml.forms.forms.TrailForm;
import elgatopro300.bbs_cml.forms.forms.VanillaParticleForm;
import elgatopro300.bbs_cml.forms.forms.StructureForm;
import elgatopro300.bbs_cml.forms.forms.LightForm;
import elgatopro300.bbs_cml.forms.renderers.AnchorFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.BillboardFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.BlockFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.ExtrudedFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.FluidFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.FormRenderer;
import elgatopro300.bbs_cml.forms.renderers.FormRenderingContext;
import elgatopro300.bbs_cml.forms.renderers.FramebufferFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.ItemFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.LabelFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.MobFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.ModelFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.ParticleFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.TrailFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.VanillaParticleFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.StructureFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.LightFormRenderer;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;

public class FormUtilsClient
{
    private static Map<Class, IFormRendererFactory> map = new HashMap<>();
    private static CustomVertexConsumerProvider customVertexConsumerProvider;
    private static Stack<Form> currentForm = new Stack<>();

    static
    {
        BlockBufferBuilderStorage storage = new BlockBufferBuilderStorage();
        SortedMap sortedMap = Util.make(new Object2ObjectLinkedOpenHashMap(), map -> {
            map.put(TexturedRenderLayers.getEntitySolid(), storage.get(RenderLayer.getSolid()));
            map.put(TexturedRenderLayers.getEntityCutout(), storage.get(RenderLayer.getCutout()));
            map.put(TexturedRenderLayers.getBannerPatterns(), storage.get(RenderLayer.getCutoutMipped()));
            map.put(TexturedRenderLayers.getEntityTranslucentCull(), storage.get(RenderLayer.getTranslucent()));
            assignBufferBuilder(map, RenderLayer.getSolid());
            assignBufferBuilder(map, RenderLayer.getCutout());
            assignBufferBuilder(map, RenderLayer.getTranslucent());
            assignBufferBuilder(map, RenderLayer.getCutoutMipped());
            assignBufferBuilder(map, TexturedRenderLayers.getShieldPatterns());
            assignBufferBuilder(map, TexturedRenderLayers.getBeds());
            assignBufferBuilder(map, TexturedRenderLayers.getShulkerBoxes());
            assignBufferBuilder(map, TexturedRenderLayers.getSign());
            assignBufferBuilder(map, TexturedRenderLayers.getHangingSign());
            map.put(TexturedRenderLayers.getChest(), new BufferBuilder(786432));
            assignBufferBuilder(map, RenderLayer.getArmorGlint());
            assignBufferBuilder(map, RenderLayer.getArmorEntityGlint());
            assignBufferBuilder(map, RenderLayer.getGlint());
            assignBufferBuilder(map, RenderLayer.getDirectGlint());
            assignBufferBuilder(map, RenderLayer.getGlintTranslucent());
            assignBufferBuilder(map, RenderLayer.getEntityGlint());
            assignBufferBuilder(map, RenderLayer.getDirectEntityGlint());
            assignBufferBuilder(map, RenderLayer.getWaterMask());
            ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(renderLayer -> assignBufferBuilder(map, renderLayer));
        });

        customVertexConsumerProvider = new CustomVertexConsumerProvider(new BufferBuilder(1536), sortedMap);

        register(BillboardForm.class, BillboardFormRenderer::new);
        register(FluidForm.class, FluidFormRenderer::new);
        register(ExtrudedForm.class, ExtrudedFormRenderer::new);
        register(LabelForm.class, LabelFormRenderer::new);
        register(ModelForm.class, ModelFormRenderer::new);
        register(ParticleForm.class, ParticleFormRenderer::new);
        register(BlockForm.class, BlockFormRenderer::new);
        register(ItemForm.class, ItemFormRenderer::new);
        register(AnchorForm.class, AnchorFormRenderer::new);
        register(MobForm.class, MobFormRenderer::new);
        register(VanillaParticleForm.class, VanillaParticleFormRenderer::new);
        register(TrailForm.class, TrailFormRenderer::new);
        register(FramebufferForm.class, FramebufferFormRenderer::new);
        register(StructureForm.class, StructureFormRenderer::new);
        register(LightForm.class, LightFormRenderer::new);
    }

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer)
    {
        builderStorage.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
    }

    public static CustomVertexConsumerProvider getProvider()
    {
        return customVertexConsumerProvider;
    }

    public static <T extends Form> void register(Class<T> clazz, IFormRendererFactory<T> function)
    {
        map.put(clazz, function);
    }

    public static Form getCurrentForm()
    {
        return currentForm.isEmpty() ? null : currentForm.peek();
    }

    public static FormRenderer getRenderer(Form form)
    {
        if (form == null)
        {
            return null;
        }

        if (form.getRenderer() instanceof FormRenderer renderer)
        {
            return renderer;
        }

        IFormRendererFactory factory = map.get(form.getClass());

        if (factory != null)
        {
            FormRenderer formRenderer = factory.create(form);

            form.setRenderer(formRenderer);

            return formRenderer;
        }

        return null;
    }

    public static void renderUI(Form form, UIContext context, int x1, int y1, int x2, int y2)
    {
        FormRenderer renderer = getRenderer(form);

        if (renderer != null)
        {
            renderer.renderUI(context, x1, y1, x2, y2);
        }
    }

    public static void render(Form form, FormRenderingContext context)
    {
        FormRenderer renderer = getRenderer(form);

        if (renderer != null)
        {
            currentForm.push(form);

            try
            {
                renderer.render(context);
            }
            catch (Exception e)
            {}

            currentForm.pop();
        }
    }

    public static List<String> getBones(Form form)
    {
        FormRenderer renderer = getRenderer(form);

        if (renderer != null)
        {
            return renderer.getBones();
        }

        return Collections.emptyList();
    }

    public static interface IFormRendererFactory <T extends Form>
    {
        public FormRenderer<T> create(T form);
    }
}
