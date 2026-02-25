package elgatopro300.bbs_cml.ui.film.controller;

import elgatopro300.bbs_cml.film.BaseFilmController;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.FilmControllerContext;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.ITickable;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.entities.MCEntity;
import elgatopro300.bbs_cml.forms.entities.StubEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.renderers.FormRenderer;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.settings.values.ui.ValueOnionSkin;
import elgatopro300.bbs_cml.utils.CollectionUtils;
import elgatopro300.bbs_cml.utils.Pair;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeChannel;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeSegment;
// import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import elgatopro300.bbs_cml.mixin.client.RenderTickCounterAccessor;
import java.util.List;
import java.util.Map;

public class FilmEditorController extends BaseFilmController
{
    public UIFilmController controller;

    private int lastTick;

    public FilmEditorController(Film film, UIFilmController controller)
    {
        super(film);

        this.controller = controller;
    }

    @Override
    public Map<String, Integer> getActors()
    {
        return this.controller.getActors();
    }

    @Override
    public int getTick()
    {
        return this.controller.panel.getCursor();
    }

    @Override
    protected void updateEntities(int ticks)
    {
        ticks = this.getTick() + (this.controller.panel.getRunner().isRunning() ? 1 : 0);

        super.updateEntities(ticks);

        this.lastTick = ticks;
    }

    @Override
    protected void updateEntityAndForm(IEntity entity, int tick)
    {
        boolean isPlaying = this.controller.isPlaying();
        boolean isActor = !(entity instanceof MCEntity);

        if (isPlaying && isActor)
        {
            super.updateEntityAndForm(entity, tick);
        }
    }

    @Override
    protected void applyReplay(Replay replay, int ticks, IEntity entity)
    {
        List<String> groups = this.controller.getRecordingGroups();
        boolean isPlaying = this.controller.isPlaying();
        boolean isActor = !(entity instanceof MCEntity);

        if (entity != this.controller.getControlled() || (this.controller.isRecording() && this.controller.getRecordingCountdown() <= 0 && groups != null))
        {
            replay.keyframes.apply(ticks, entity, entity == this.controller.getControlled() ? groups : null);
            replay.applyClientActions(ticks, entity, this.film);
        }

        if (entity == this.controller.getControlled() && this.controller.isRecording() && this.controller.panel.getRunner().isRunning())
        {
            replay.keyframes.record(this.controller.panel.getCursor(), entity, groups);
        }

        ticks = this.getTick() + (this.controller.panel.getRunner().isRunning() ? 1 : 0);

        /* Special pausing logic */
        if (!isPlaying && isActor)
        {
            entity.setPrevX(entity.getX());
            entity.setPrevY(entity.getY());
            entity.setPrevZ(entity.getZ());
            entity.setPrevYaw(entity.getYaw());
            entity.setPrevHeadYaw(entity.getHeadYaw());
            entity.setPrevBodyYaw(entity.getBodyYaw());
            entity.setPrevPitch(entity.getPitch());

            int diff = Math.abs(this.lastTick - ticks);

            while (diff > 0)
            {
                entity.update();

                if (entity.getForm() != null)
                {
                    entity.getForm().update(entity);
                }

                diff -= 1;
            }
        }
    }

    @Override
    protected float getTransition(IEntity entity, float transition)
    {
        boolean current = this.isCurrent(entity) && this.controller.isControlling();
        float delta = !this.controller.isPlaying() && !current ? 0F : transition;

        return delta;
    }

    @Override
    protected boolean canUpdate(int i, Replay replay, IEntity entity, UpdateMode updateMode)
    {
        return super.canUpdate(i, replay, entity, updateMode)
            || this.controller.getPovMode() != UIFilmController.CAMERA_MODE_FIRST_PERSON
            || !this.isCurrent(entity)
            || !this.controller.orbit.enabled;
    }

    /*
    @Override
    protected void renderEntity(WorldRenderContext context, Replay replay, IEntity entity)
    {
        boolean current = this.isCurrent(entity);

        if (!(this.controller.getPovMode() == UIFilmController.CAMERA_MODE_FIRST_PERSON && current))
        {
            super.renderEntity(context, replay, entity);
        }

        boolean isPlaying = this.controller.isPlaying();
        int ticks = replay.getTick(this.getTick());
        ValueOnionSkin onionSkin = this.controller.getOnionSkin();
        BaseValue value = replay.properties.get(onionSkin.group.get());

        if (value == null)
        {
            value = replay.properties.get("pose");
        }

        if (value instanceof KeyframeChannel<?> pose && entity instanceof StubEntity)
        {
            boolean canRender = onionSkin.enabled.get();

            if (!onionSkin.all.get())
            {
                canRender = canRender && current;
            }

            if (canRender)
            {
                KeyframeSegment<?> segment = pose.findSegment(ticks);

                if (segment != null)
                {
                    this.renderOnion(replay, pose.getKeyframes().indexOf(segment.a), -1, pose, onionSkin.preColor.get(), onionSkin.preFrames.get(), context, isPlaying, entity);
                    this.renderOnion(replay, pose.getKeyframes().indexOf(segment.b), 1, pose, onionSkin.postColor.get(), onionSkin.postFrames.get(), context, isPlaying, entity);

                    replay.keyframes.apply(ticks, entity);
                    float tick = ticks + this.getTransition(entity, ((RenderTickCounterAccessor) context.tickCounter()).getTickDeltaField());
                    Form form = entity.getForm();
                    replay.properties.applyProperties(form, tick);

                    if (!isPlaying)
                    {
                        entity.setPrevX(entity.getX());
                        entity.setPrevY(entity.getY());
                        entity.setPrevZ(entity.getZ());
                        entity.setPrevYaw(entity.getYaw());
                        entity.setPrevHeadYaw(entity.getHeadYaw());
                        entity.setPrevBodyYaw(entity.getBodyYaw());
                        entity.setPrevPitch(entity.getPitch());
                    }
                }
            }
        }
    }
    */

    /*
    private void renderOnion(Replay replay, int index, int direction, KeyframeChannel<?> pose, int color, int frames, WorldRenderContext context, boolean isPlaying, IEntity entity)
    {
        // Disabled
    }
    */

    /*
    @Override
    protected FilmControllerContext getFilmControllerContext(WorldRenderContext context, Replay replay, IEntity entity)
    {
        Pair<String, Boolean> bone = this.isCurrent(entity) && !this.controller.panel.recorder.isRecording() ? this.controller.getBone() : null;
        String aBone = bone == null ? null : bone.a;
        boolean local = bone != null && bone.b;
        String aBone2 = null;
        boolean local2 = false;

        if (replay.axesPreview.get())
        {
            aBone2 = replay.axesPreviewBone.get();
            local2 = true;
        }

        if (this.controller.panel.recorder.isRecording())
        {
            aBone = null;
            local = false;
            aBone2 = null;
            local2 = false;
        }

        return super.getFilmControllerContext(context, replay, entity)
            .transition(this.getTransition(entity, ((RenderTickCounterAccessor) context.tickCounter()).getTickDeltaField()))
            .bone(aBone, local)
            .bone2(aBone2, local2);
    }
    */

    private boolean isCurrent(IEntity entity)
    {
        return entity == this.controller.getCurrentEntity();
    }
}