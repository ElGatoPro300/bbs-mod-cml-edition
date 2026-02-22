package elgatopro300.bbs_cml.mixin.client.iris;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.film.Films;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import elgatopro300.bbs_cml.film.BaseFilmController;
import elgatopro300.bbs_cml.film.FilmControllerContext;
import elgatopro300.bbs_cml.film.Recorder;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.UIDashboardPanel;
import elgatopro300.bbs_cml.ui.film.UIFilmPanel;
import elgatopro300.bbs_cml.ui.film.controller.FilmEditorController;
import elgatopro300.bbs_cml.ui.film.controller.UIFilmController;
import elgatopro300.bbs_cml.ui.framework.UIBaseMenu;
import elgatopro300.bbs_cml.ui.framework.UIScreen;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.settings.values.ui.ValueOnionSkin;
import elgatopro300.bbs_cml.forms.entities.StubEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeChannel;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeSegment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.Frustum;
// // import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
@Mixin(value = ShadowRenderer.class, remap = false)
public class ShadowRendererMixin
{
    @Inject(method = "renderEntities", at = @At("TAIL"))
    private void bbs$renderFormsShadows(LevelRendererAccessor levelRenderer,
                                        EntityRenderManager dispatcher,
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
        Camera gameCamera = MinecraftClient.getInstance().gameRenderer.getCamera();
        // RenderSystem.enableDepthTest();

        /* Case 1: film panel open – keep existing onion skin and panel-specific logic */
        if (menu instanceof UIDashboard)
        {
            UIDashboard dashboard = (UIDashboard) menu;
            UIDashboardPanel panel = dashboard.getPanels().panel;

            if (panel instanceof UIFilmPanel)
            {
                UIFilmPanel filmPanel = (UIFilmPanel) panel;
                UIFilmController controller = filmPanel.getController();

                if (controller != null && controller.editorController != null)
                {
                    FilmEditorController editorController = controller.editorController;
                    if (editorController.film != null)
                    {
                        boolean isPlaying = !controller.isPaused() && filmPanel.isRunning();
                        float transition = isPlaying ? tickDelta : 0.0F;
                        List<Replay> replays = editorController.film.replays.getList();

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
                                .relative((Boolean) replay.relative.get())
                                .isShadowPass(true)
                                .viewMatrix(new Matrix4f(shadowStack.peek().getPositionMatrix()));

                            BaseFilmController.renderEntity(context);

                            // Onion skin ghost shadows (Ctrl preview)
                            ValueOnionSkin onionSkin = controller.getOnionSkin();
                            BaseValue value = replay.properties.get(onionSkin.group.get());

                            if (value == null)
                            {
                                value = replay.properties.get("pose");
                            }

                            if (value instanceof KeyframeChannel<?> pose && entity instanceof StubEntity)
                            {
                                boolean current = entity == controller.getCurrentEntity();
                                boolean canRenderOnion = onionSkin.enabled.get();

                                if (!onionSkin.all.get())
                                {
                                    canRenderOnion = canRenderOnion && current;
                                }

                                if (canRenderOnion)
                                {
                                    int ticks = replay.getTick(controller.panel.getCursor());
                                    KeyframeSegment<?> segment = pose.findSegment(ticks);

                                    if (segment != null)
                                    {
                                        // Pre frames
                                        renderOnionGhostShadows(editorController, controller, entity, replay, (KeyframeChannel<?>) pose, -1, shadowStack, consumers, gameCamera);
                                        // Post frames
                                        renderOnionGhostShadows(editorController, controller, entity, replay, (KeyframeChannel<?>) pose, 1, shadowStack, consumers, gameCamera);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        /* Case 2: no panel – render shadows for active film controllers and recorder */
        else
        {
            Films films = BBSModClient.getFilms();

            // Active film controllers playing in world
            for (BaseFilmController controller : films.getControllers())
            {
                float transition = controller.paused ? 0.0F : tickDelta;
                List<Replay> replays = controller.film.replays.getList();

                Iterator<Entry<Integer, IEntity>> it = controller.getEntities().entrySet().iterator();
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
                        .setup(controller.getEntities(), entity, replay, gameCamera, shadowStack, consumers, transition)
                        .shadow((Boolean) replay.shadow.get(), (Float) replay.shadowSize.get())
                        .relative((Boolean) replay.relative.get())
                        .isShadowPass(true);

                    BaseFilmController.renderEntity(context);
                }
            }

            // Active recorder shadows while recording
            Recorder recorder = films.getRecorder();
            if (recorder != null)
            {
                float transition = recorder.paused ? 0.0F : tickDelta;
                List<Replay> replays = recorder.film.replays.getList();

                Iterator<Entry<Integer, IEntity>> it = recorder.getEntities().entrySet().iterator();
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
                        .setup(recorder.getEntities(), entity, replay, gameCamera, shadowStack, consumers, transition)
                        .shadow((Boolean) replay.shadow.get(), (Float) replay.shadowSize.get())
                        .relative((Boolean) replay.relative.get())
                        .viewMatrix(new Matrix4f(shadowStack.peek().getPositionMatrix()));

                    BaseFilmController.renderEntity(context);
                }
            }
        }

        consumers.draw();
    }

    private static void renderOnionGhostShadows(FilmEditorController editorController,
                                                UIFilmController controller,
                                                IEntity entity,
                                                Replay replay,
                                                KeyframeChannel<?> pose,
                                                int direction,
                                                MatrixStack shadowStack,
                                                VertexConsumerProvider.Immediate consumers,
                                                Camera camera)
    {
        int cursor = controller.panel.getCursor();
        int frames = direction < 0 ? controller.getOnionSkin().preFrames.get() : controller.getOnionSkin().postFrames.get();
        List<? extends Keyframe<?>> keyframes = pose.getKeyframes();

        // Find current segment index around cursor
        int index = -1;
        for (int i = 0; i < keyframes.size(); i++)
        {
            if ((int) keyframes.get(i).getTick() == cursor)
            {
                index = i;
                break;
            }
        }

        if (index == -1)
        {
            // Fallback: nearest index
            index = 0;
        }

        while (index >= 0 && index < keyframes.size() && frames > 0)
        {
            Keyframe<?> keyframe = keyframes.get(index);

            if ((int) keyframe.getTick() == cursor)
            {
                index += direction;
                continue;
            }

            int tick1 = (int) keyframe.getTick();
            replay.keyframes.apply(tick1, entity);
            float tick = (int) keyframe.getTick();
            Form form = entity.getForm();
            replay.properties.applyProperties(form, tick);

            FilmControllerContext ctx = FilmControllerContext.instance
                .setup(editorController.getEntities(), entity, replay, camera, shadowStack, consumers, 0F)
                .shadow((Boolean) replay.shadow.get(), (Float) replay.shadowSize.get())
                .relative((Boolean) replay.relative.get())
                .viewMatrix(new Matrix4f(shadowStack.peek().getPositionMatrix()));

            BaseFilmController.renderEntity(ctx);

            frames -= 1;
            index += direction;
        }
    }
}
