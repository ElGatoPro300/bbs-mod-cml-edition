package mchorse.bbs_mod.mixin.client.iris;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import mchorse.bbs_mod.film.BaseFilmController;
import mchorse.bbs_mod.film.FilmControllerContext;
import mchorse.bbs_mod.film.replays.Replay;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.film.controller.FilmEditorController;
import mchorse.bbs_mod.ui.film.controller.UIFilmController;
import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.UIScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = ShadowRenderer.class, remap = false)
public class ShadowRendererMixin
{
    @Inject(method = "renderEntities", at = @At("TAIL"))
    private void bbs$renderFormsShadows(LevelRendererAccessor levelRenderer,
                                        EntityRenderDispatcher dispatcher,
                                        VertexConsumerProvider.Immediate consumers,
                                        MatrixStack shadowStack,
                                        float tickDelta,
                                        Frustum frustum,
                                        double camX,
                                        double camY,
                                        double camZ,
                                        CallbackInfoReturnable<Integer> cir)
    {
        if (!ShadowRenderer.ACTIVE)
        {
            return;
        }

        UIBaseMenu menu = UIScreen.getCurrentMenu();
        if (!(menu instanceof UIDashboard))
        {
            return;
        }

        UIDashboard dashboard = (UIDashboard) menu;
        UIDashboardPanel panel = dashboard.getPanels().panel;
        if (!(panel instanceof UIFilmPanel))
        {
            return;
        }

        UIFilmPanel filmPanel = (UIFilmPanel) panel;
        UIFilmController controller = filmPanel.getController();
        if (controller == null || controller.editorController == null)
        {
            return;
        }

        FilmEditorController editorController = controller.editorController;
        if (editorController.film == null)
        {
            return;
        }

        boolean isPlaying = !controller.isPaused() && filmPanel.isRunning();
        float transition = isPlaying ? tickDelta : 0.0F;
        List<Replay> replays = editorController.film.replays.getList();
        Camera gameCamera = MinecraftClient.getInstance().gameRenderer.getCamera();

        RenderSystem.enableDepthTest();

        Iterator<Entry<Integer, IEntity>> it = editorController.getEntities().entrySet().iterator();
        while (it.hasNext())
        {
            Entry<Integer, IEntity> entry = it.next();
            int index = entry.getKey();
            IEntity entity = entry.getValue();

            if (index < 0 || index >= replays.size())
            {
                continue;
            }

            Replay replay = replays.get(index);
            if ((Boolean) replay.actor.get())
            {
                continue;
            }

            if (entity.getForm() != null && !((Boolean) entity.getForm().shaderShadow.get()))
            {
                continue;
            }

            FilmControllerContext context = FilmControllerContext.instance
                .setup(editorController.getEntities(), entity, replay, gameCamera, shadowStack, consumers, transition)
                .shadow((Boolean) replay.shadow.get(), (Float) replay.shadowSize.get())
                .relative((Boolean) replay.relative.get());

            BaseFilmController.renderEntity(context);
        }

        consumers.draw();
    }
}
