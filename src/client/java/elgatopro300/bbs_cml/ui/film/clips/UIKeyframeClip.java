package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.camera.clips.overwrite.KeyframeClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditor;
import elgatopro300.bbs_cml.ui.film.utils.keyframes.UIFilmKeyframes;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframeEditor;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.MathUtils;
import elgatopro300.bbs_cml.utils.clips.Clips;
import elgatopro300.bbs_cml.utils.joml.Matrices;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeChannel;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeSegment;
import org.joml.Vector3f;

public class UIKeyframeClip extends UIClip<KeyframeClip>
{
    public UIButton edit;
    public UIKeyframeEditor keyframes;
    public UIToggle additive;

    public UIKeyframeClip(KeyframeClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void addEnvelopes()
    {
        super.addEnvelopes();

        this.additive = new UIToggle(UIKeys.CAMERA_PANELS_ADDITIVE, (b) ->
        {
            this.clip.additive.set(b.getValue());
        });

        this.panels.add(this.additive);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.keyframes = new UIKeyframeEditor((consumer) -> new UIFilmKeyframes(this.editor, consumer));
        this.keyframes.view.backgroundRenderer((context) ->
        {
            UIReplaysEditor.renderBackground(context, this.keyframes.view, (Clips) this.clip.getParent(), this.clip.tick.get());
        });
        this.keyframes.view.duration(() -> this.clip.duration.get());
        this.keyframes.setUndoId("keyframe_keyframes");

        this.edit = new UIButton(UIKeys.GENERAL_EDIT, (b) ->
        {
            this.editor.embedView(this.keyframes);
            this.keyframes.view.resetView();
            this.keyframes.view.getGraph().clearSelection();
        });
        this.edit.keys().register(Keys.FORMS_EDIT, () -> this.edit.clickItself());
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_KEYFRAMES), this.edit).marginTop(12));
    }

    @Override
    public void editClip(Position position)
    {
        Position newPos = position.copy();
        long tick = this.editor.getCursor() - this.clip.tick.get();

        if (!this.clip.distance.isEmpty())
        {
            double distance = this.clip.distance.interpolate(tick);

            if (distance != 0D)
            {
                Vector3f rotation = Matrices.rotation(
                    MathUtils.toRad(newPos.angle.pitch),
                    MathUtils.toRad(-newPos.angle.yaw - 180)
                );

                newPos.point.x -= rotation.x * distance;
                newPos.point.y -= rotation.y * distance;
                newPos.point.z -= rotation.z * distance;
            }
        }

        this.insertKeyframe(tick, this.clip.x, newPos.point.x);
        this.insertKeyframe(tick, this.clip.y, newPos.point.y);
        this.insertKeyframe(tick, this.clip.z, newPos.point.z);
        this.insertKeyframe(tick, this.clip.yaw, newPos.angle.yaw);
        this.insertKeyframe(tick, this.clip.pitch, newPos.angle.pitch);
        this.insertKeyframe(tick, this.clip.roll, newPos.angle.roll);
        this.insertKeyframe(tick, this.clip.fov, newPos.angle.fov);
    }

    private void insertKeyframe(long tick, KeyframeChannel<Double> channel, double x)
    {
        KeyframeSegment<Double> segment = channel.findSegment(tick);
        int insert = channel.insert(tick, x);

        if (segment != null)
        {
            channel.get(insert).copyOverExtra(segment.a);
        }
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.updateDuration(this.clip.duration.get());
        this.keyframes.setClip(this.clip);
        this.additive.setValue(this.clip.additive.get());
    }

    @Override
    public void applyUndoData(MapType data)
    {
        super.applyUndoData(data);

        if (data.getString("embed").equals("keyframe"))
        {
            this.editor.embedView(this.keyframes);
            this.keyframes.view.resetView();
        }
    }

    @Override
    public void collectUndoData(MapType data)
    {
        super.collectUndoData(data);

        if (this.keyframes.hasParent())
        {
            data.putString("embed", "keyframe");
        }
    }
}