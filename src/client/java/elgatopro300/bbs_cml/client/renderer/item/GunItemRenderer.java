package elgatopro300.bbs_cml.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.entities.StubEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.renderers.FormRenderType;
import elgatopro300.bbs_cml.forms.renderers.FormRenderingContext;
import elgatopro300.bbs_cml.items.GunProperties;
import elgatopro300.bbs_cml.ui.framework.UIScreen;
import elgatopro300.bbs_cml.ui.model_blocks.UIModelBlockEditorMenu;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.pose.Transform;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GunItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer
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
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Item item = this.get(stack);

        if (item != null)
        {
            GunProperties properties = item.properties;
            Form form = properties.getForm(mode);
            Transform transform = properties.getTransform(mode);
            boolean zoom = mode.isFirstPerson() && BBSModClient.getGunZoom() != null && properties.getZoomForm() != null;

            if (zoom)
            {
                form = properties.getZoomForm();
                transform = properties.zoomTransform;
            }

            /* Preview zoom form */
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

                RenderSystem.enableDepthTest();

                if (mode == ModelTransformationMode.GUI)
                {
                    Vector3f a = new Vector3f(0.85F, 0.85F, -1F).normalize();
                    Vector3f b = new Vector3f(-0.85F, 0.85F, 1F).normalize();
                    RenderSystem.setupLevelDiffuseLighting(a, b);
                }

                int maxLight = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE;
                FormUtilsClient.render(form, new FormRenderingContext()
                    .set(FormRenderType.fromModelMode(mode), item.formEntity, matrices, maxLight, overlay, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false))
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
