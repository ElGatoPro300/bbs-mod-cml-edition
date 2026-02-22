package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.actions.types.AttackActionClip;
import elgatopro300.bbs_cml.actions.types.DamageActionClip;
import elgatopro300.bbs_cml.actions.types.SwipeActionClip;
import elgatopro300.bbs_cml.actions.types.blocks.BreakBlockActionClip;
import elgatopro300.bbs_cml.actions.types.blocks.InteractBlockActionClip;
import elgatopro300.bbs_cml.actions.types.blocks.PlaceBlockActionClip;
import elgatopro300.bbs_cml.actions.types.chat.ChatActionClip;
import elgatopro300.bbs_cml.actions.types.chat.CommandActionClip;
import elgatopro300.bbs_cml.actions.types.item.ItemDropActionClip;
import elgatopro300.bbs_cml.actions.types.item.UseBlockItemActionClip;
import elgatopro300.bbs_cml.actions.types.item.UseItemActionClip;
import elgatopro300.bbs_cml.camera.clips.misc.AudioClientClip;
import elgatopro300.bbs_cml.camera.clips.misc.CurveClientClip;
import elgatopro300.bbs_cml.camera.clips.misc.SubtitleClip;
import elgatopro300.bbs_cml.camera.clips.misc.TrackerClientClip;
import elgatopro300.bbs_cml.camera.clips.misc.VideoClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.AngleClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.DollyZoomClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.DragClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.LookClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.MathClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.OrbitClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.RemapperClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.ShakeClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.TranslateClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.DollyClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.IdleClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.KeyframeClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.PathClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.camera.utils.TimeUtils;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIAttackActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIBreakBlockActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIChatActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UICommandActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIDamageActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIInteractBlockActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIItemDropActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIPlaceBlockActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UISwipeActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIUseBlockItemActionClip;
import elgatopro300.bbs_cml.ui.film.clips.actions.UIUseItemActionClip;
import elgatopro300.bbs_cml.ui.film.clips.widgets.UIEnvelope;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UILabel;
import elgatopro300.bbs_cml.ui.utils.ScrollDirection;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.TimeUtilsClient;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.undo.IUndo;

import java.util.HashMap;
import java.util.Map;

public abstract class UIClip <T extends Clip> extends UIElement
{
    private static final Map<Class, IUIClipFactory> FACTORIES = new HashMap<>();
    private static final Map<Class, Integer> SCROLLS = new HashMap<>();

    public T clip;
    public IUIClipsDelegate editor;

    public UIToggle enabled;
    public UITextbox title;
    public UITrackpad layer;
    public UITrackpad tick;
    public UITrackpad duration;

    public UIEnvelope envelope;

    public UIScrollView panels;

    static
    {
        register(IdleClip.class, UIIdleClip::new);
        register(DollyClip.class, UIDollyClip::new);
        register(PathClip.class, UIPathClip::new);
        register(KeyframeClip.class, UIKeyframeClip::new);
        register(TranslateClip.class, UITranslateClip::new);
        register(AngleClip.class, UIAngleClip::new);
        register(DragClip.class, UIDragClip::new);
        register(ShakeClip.class, UIShakeClip::new);
        register(MathClip.class, UIMathClip::new);
        register(LookClip.class, UILookClip::new);
        register(TrackerClientClip.class, UITrackerClip::new);
        register(OrbitClip.class, UIOrbitClip::new);
        register(RemapperClip.class, UIRemapperClip::new);
        register(AudioClientClip.class, UIAudioClip::new);
        register(VideoClip.class, UIVideoClip::new);
        register(SubtitleClip.class, UISubtitleClip::new);
        register(CurveClientClip.class, UICurveClip::new);
        register(DollyZoomClip.class, UIDollyZoomClip::new);

        register(ChatActionClip.class, UIChatActionClip::new);
        register(CommandActionClip.class, UICommandActionClip::new);
        register(PlaceBlockActionClip.class, UIPlaceBlockActionClip::new);
        register(InteractBlockActionClip.class, UIInteractBlockActionClip::new);
        register(BreakBlockActionClip.class, UIBreakBlockActionClip::new);
        register(UseItemActionClip.class, UIUseItemActionClip::new);
        register(UseBlockItemActionClip.class, UIUseBlockItemActionClip::new);
        register(AttackActionClip.class, UIAttackActionClip::new);
        register(DamageActionClip.class, UIDamageActionClip::new);
        register(ItemDropActionClip.class, UIItemDropActionClip::new);
        register(SwipeActionClip.class, UISwipeActionClip::new);
    }

    public static <T extends Clip> void register(Class<T> clazz, IUIClipFactory<T> factory)
    {
        FACTORIES.put(clazz, factory);
    }

    public static void saveScroll(UIClip editor)
    {
        if (editor != null)
        {
            SCROLLS.put(editor.clip.getClass(), (int) editor.panels.scroll.getScroll());
        }
    }

    public static UIClip createPanel(Clip clip, IUIClipsDelegate delegate)
    {
        IUIClipFactory factory = FACTORIES.get(clip.getClass());
        UIClip clipEditor = factory == null ? null : factory.create(clip, delegate);

        if (clipEditor != null)
        {
            clipEditor.panels.scroll.setScroll(SCROLLS.getOrDefault(clip.getClass(), 0));
        }

        return clipEditor;
    }

    public static UILabel label(IKey key)
    {
        return UI.label(key).background(() -> BBSSettings.primaryColor(Colors.A50));
    }

    public UIClip(T clip, IUIClipsDelegate editor)
    {
        this.clip = clip;
        this.editor = editor;

        this.enabled = new UIToggle(UIKeys.CAMERA_PANELS_ENABLED, (b) -> this.editor.editMultiple(this.clip.enabled, (value) ->
        {
            value.set(b.getValue());
        }));
        this.title = new UITextbox(1000, (t) -> this.clip.title.set(t));
        this.title.tooltip(UIKeys.CAMERA_PANELS_TITLE_TOOLTIP);
        this.layer = new UITrackpad((v) -> this.editor.editMultiple(this.clip.layer, v.intValue()));
        this.layer.limit(0, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_LAYER);
        this.tick = new UITrackpad((v) -> this.editor.editMultiple(this.clip.tick, (int) TimeUtils.fromTime(v)));
        this.tick.limit(0, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_TICK);
        this.duration = new UITrackpad((v) ->
        {
            this.editor.editMultiple(this.clip.duration, (int) TimeUtils.fromTime(v));
            this.updateDuration((int) TimeUtils.fromTime(v));
        });
        this.duration.limit(1, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_DURATION);
        this.envelope = new UIEnvelope(this);
        this.envelope.channel.setUndoId("envelope_keyframes");

        boolean horizontal = BBSSettings.editorHorizontalClipEditor.get();

        this.panels = new UIScrollView(horizontal ? ScrollDirection.HORIZONTAL : ScrollDirection.VERTICAL);
        this.panels.scroll.cancelScrolling();

        if (horizontal)
        {
            this.panels.full(this).column(5).scroll().width(140).padding(10);
        }
        else
        {
            this.panels.full(this).column(5).scroll().vertical().stretch().padding(10);
        }

        this.registerUI();
        this.registerPanels();

        this.add(this.panels);
    }

    protected void registerUI()
    {}

    protected void registerPanels()
    {
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_TITLE), this.title);
        this.panels.add(this.enabled.marginBottom(6));
        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_METRICS), UI.row(this.layer, this.tick), this.duration));

        this.addEnvelopes();
    }

    protected void addEnvelopes()
    {
        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_ENVELOPES_TITLE), this.envelope).marginTop(12));
    }

    public void handleUndo(IUndo<ValueGroup> undo, boolean redo)
    {
        this.fillData();
    }

    protected void updateDuration(int duration)
    {}

    public void editClip(Position position)
    {
        this.fillData();
    }

    public void fillData()
    {
        TimeUtilsClient.configure(this.tick, 0);
        TimeUtilsClient.configure(this.duration, 1);

        this.enabled.setValue(this.clip.enabled.get());
        this.title.setText(this.clip.title.get());
        this.layer.setValue(this.clip.layer.get());
        this.tick.setValue(TimeUtils.toTime(this.clip.tick.get()));
        this.duration.setValue(TimeUtils.toTime(this.clip.duration.get()));
        this.envelope.fillData();
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.gradientHBox(this.area.x - 40, this.area.y, this.area.ex() - 40, this.area.ey(), 0, Colors.A25);
        context.batcher.box(this.area.ex() - 40, this.area.y, this.area.ex(), this.area.ey(), Colors.A25);

        super.render(context);
    }

    @Override
    public void applyUndoData(MapType data)
    {
        super.applyUndoData(data);

        if (data.getString("embed").equals("envelope"))
        {
            this.editor.embedView(this.envelope.channel);
            this.envelope.channel.view.editSheet(this.envelope.channel.view.getGraph().getSheets().get(0));
            this.envelope.channel.view.resetView();
        }
    }

    @Override
    public void collectUndoData(MapType data)
    {
        super.collectUndoData(data);

        if (this.envelope.channel.hasParent())
        {
            data.putString("embed", "envelope");
        }
    }

    public static interface IUIClipFactory <T extends Clip>
    {
        public UIClip create(T clip, IUIClipsDelegate delegate);
    }
}