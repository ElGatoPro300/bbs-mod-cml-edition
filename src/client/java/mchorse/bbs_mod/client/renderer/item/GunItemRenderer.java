package elgatopro300.bbs_cml.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.opengl.GlStateManager;
import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.entities.StubEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.renderers.FormRenderType;
import elgatopro300.bbs_cml.forms.renderers.FormRenderingContext;
import elgatopro300.bbs_cml.forms.renderers.FormRenderer;
import elgatopro300.bbs_cml.items.GunItem;
import elgatopro300.bbs_cml.items.GunProperties;
import elgatopro300.bbs_cml.ui.framework.UIScreen;
import elgatopro300.bbs_cml.ui.model_blocks.UIModelBlockEditorMenu;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.pose.Transform;
import elgatopro300.bbs_cml.items.ItemDisplayMode;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GunItemRenderer implements SpecialModelRenderer<ItemStack>
{
    private Map<ItemStack, Item> map = new HashMap<>();

    public void update()
    {
        Iterator<Item> it = this.map.values().iterator();

        while (it.hasNext())
        {
            Item item = it.next();

            if (item.expiration <= 0)
            {
                it.remove();
            }

            item.expiration -= 1;
            item.properties.update(item.formEntity);
            item.formEntity.update();
        }
    }

    @Override
    public ItemStack getData(ItemStack stack)
    {
        return stack;
    }

    /*
    // Legacy render method retained for reference
    public void render(ItemStack data, ItemDisplayContext mode, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, int overlay, boolean hasGlint, int glintAlpha)
    {
        Item item = this.get(data);

        if (item != null)
        {
            ItemDisplayMode displayMode = ItemDisplayMode.NONE;

            GunProperties properties = item.properties;
            Form form = properties.getForm(displayMode);
            Transform transform = properties.getTransform(displayMode);
            boolean zoom = false;

            if (UIScreen.getCurrentMenu() instanceof UIModelBlockEditorMenu editorMenu && editorMenu.currentSection == editorMenu.sectionZoom)
            {
                form = editorMenu.getGunProperties().getZoomForm();
                transform = editorMenu.getGunProperties().zoomTransform;
            }

            if (form != null)
            {
                item.expiration = 20;

                matrices.push();
                matrices.translate(0.5F, 0F, 0.5F);
                MatrixStackUtils.applyTransform(matrices, transform);

                GlStateManager._enableDepthTest();

                if (mode == ModelTransformationMode.GUI)
                {
                    Vector3f a = new Vector3f(0.85F, 0.85F, -1F).normalize();
                    Vector3f b = new Vector3f(-0.85F, 0.85F, 1F).normalize();
                    RenderSystem.setupLevelDiffuseLighting(a, b);
                }

                int maxLight = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE;
                FormUtilsClient.render(form, new FormRenderingContext()
                    .set(FormRenderType.fromModelMode(mode), item.formEntity, matrices, maxLight, overlay, ((elgatopro300.bbs_cml.mixin.client.RenderTickCounterAccessor) MinecraftClient.getInstance().getRenderTickCounter()).getTickDeltaField())
                    .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));

                if (mode == ModelTransformationMode.GUI)
                {
                    DiffuseLighting.disableGuiDepthLighting();
                }

                RenderSystem.disableDepthTest();

                matrices.pop();
            }
        }
    }
    */

    @Override
    public void render(ItemStack data, ItemDisplayContext mode, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, int overlay, boolean hasGlint, int glintAlpha)
    {
        Item item = this.get(data);

        if (item != null && item.formEntity.getForm().getRenderer() instanceof FormRenderer)
        {
            FormRenderingContext context = new FormRenderingContext();
            context.set(FormRenderType.ITEM, item.formEntity, matrices, light, overlay, 1F)
                .consumers(FormUtilsClient.getProvider());
            ((FormRenderer) item.formEntity.getForm().getRenderer()).render(context);
        }
    }

    public void collectVertices(java.util.Set<org.joml.Vector3f> vertices)
    {
    }

    @Override
    public void collectVertices(java.util.function.Consumer<org.joml.Vector3fc> consumer)
    {
    }

    /*
    public void render(ItemStack data, ItemDisplayContext mode, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, int overlay, boolean hasGlint, int glintAlpha)
    {
        Item item = this.get(data);

        if (item != null)
        {
            ItemDisplayMode displayMode = ItemDisplayMode.NONE;

            GunProperties properties = item.properties;
            Form form = properties.getForm(displayMode);
            Transform transform = properties.getTransform(displayMode);
            boolean zoom = false; // (mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || mode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) && BBSModClient.getGunZoom() != null && properties.getZoomForm() != null;

            if (zoom)
            {
                form = properties.getZoomForm();
                transform = properties.zoomTransform;
            }

            // Preview zoom form
            if (UIScreen.getCurrentMenu() instanceof UIModelBlockEditorMenu editorMenu && editorMenu.currentSection == editorMenu.sectionZoom)
            {
                form = editorMenu.getGunProperties().getZoomForm();
                transform = editorMenu.getGunProperties().zoomTransform;
            }

            if (form != null)
            {
                item.expiration = 20;

                matrices.push();
                matrices.translate(0.5F, 0F, 0.5F);
                MatrixStackUtils.applyTransform(matrices, transform);

                GlStateManager._enableDepthTest();

                Vector3f a = new Vector3f(0.85F, 0.85F, -1F).normalize();
                Vector3f b = new Vector3f(-0.85F, 0.85F, 1F).normalize();
                // RenderSystem.setupLevelDiffuseLighting(a, b);
                int maxLight = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE;
                FormUtilsClient.render(form, new FormRenderingContext()
                    .set(FormRenderType.fromModelMode(mode), item.formEntity, matrices, maxLight, overlay, ((elgatopro300.bbs_cml.mixin.client.RenderTickCounterAccessor) MinecraftClient.getInstance().getRenderTickCounter()).getTickDeltaField())
                    .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
                // DiffuseLighting.disableGuiDepthLighting();
                GlStateManager._disableDepthTest();

                matrices.pop();
            }
        }
    }
    */

    public Item get(ItemStack stack)
    {
        if (stack == null || stack.getItem() != BBSMod.GUN_ITEM)
        {
            return null;
        }

        if (this.map.containsKey(stack))
        {
            return this.map.get(stack);
        }

        Item item = new Item(GunProperties.get(stack));

        this.map.put(stack, item);

        return item;
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> getCodec()
        {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(BakeContext context)
        {
            return BBSModClient.getGunItemRenderer();
        }
    }

    public static class Item
    {
        public GunProperties properties;
        public IEntity formEntity;
        public int expiration = 20;

        public Item(GunProperties properties)
        {
            this.properties = properties;
            this.formEntity = new StubEntity(MinecraftClient.getInstance().world);
        }
    }
}
