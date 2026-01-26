package mchorse.bbs_mod.ui.film.replays;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.blocks.entities.ModelProperties;
import mchorse.bbs_mod.camera.Camera;
import mchorse.bbs_mod.camera.clips.CameraClipContext;
import mchorse.bbs_mod.camera.clips.modifiers.EntityClip;
import mchorse.bbs_mod.camera.data.Position;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.film.Film;
import mchorse.bbs_mod.film.replays.Replay;
import mchorse.bbs_mod.film.replays.Replays;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.forms.AnchorForm;
import mchorse.bbs_mod.forms.forms.BodyPart;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.MobForm;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.forms.utils.Anchor;
import mchorse.bbs_mod.graphics.window.Window;
import mchorse.bbs_mod.math.IExpression;
import mchorse.bbs_mod.math.MathBuilder;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.settings.values.IValueListener;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.settings.values.core.ValueForm;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.film.replays.overlays.UIReplaysOverlayPanel;
import mchorse.bbs_mod.ui.forms.UIFormPalette;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIList;
import mchorse.bbs_mod.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIFolderPickerOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UINumberOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icon;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.CollectionUtils;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.RayTracing;
import mchorse.bbs_mod.utils.clips.Clip;
import mchorse.bbs_mod.utils.clips.Clips;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.keyframes.Keyframe;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;
import mchorse.bbs_mod.utils.pose.Transform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.io.File;
import java.nio.file.Path;

/**
 * This GUI is responsible for drawing replays available in the
 * director thing
 */
public class UIReplayList extends UIList<Replay> {
    private static String LAST_PROCESS = "v";
    private static String LAST_OFFSET = "0";
    private static List<String> LAST_PROCESS_PROPERTIES = Arrays.asList("x");
    private static int LAST_PROCESS_SECTION = 0;
    private static int LAST_PROCESS_GRID_COLUMNS = 4;
    private static double LAST_PROCESS_GRID_SPACING_X = 2D;
    private static double LAST_PROCESS_GRID_SPACING_Z = 2D;
    private static double LAST_PROCESS_CIRCLE_RADIUS = 3D;
    private static int LAST_PROCESS_CIRCLE_COUNT = 8;
    private static double LAST_PROCESS_CIRCLE_START_ANGLE = 0D;
    private static double LAST_PROCESS_LINE_DIRECTION = 0D;
    private static double LAST_PROCESS_LINE_SPACING = 2D;
    private static double LAST_PROCESS_SCATTER_AREA_X = 10D;
    private static double LAST_PROCESS_SCATTER_AREA_Z = 10D;
    private static double LAST_PROCESS_SCATTER_SEED = 0D;
    private static double LAST_PROCESS_SCATTER_MIN_SEPARATION = 1D;
    private static int LAST_OFFSET_SECTION = 0;
    private static double LAST_OFFSET_STEP = 1D;
    private static double LAST_OFFSET_RANDOM_SEED = 0D;
    private static double LAST_OFFSET_RANDOM_MIN = -1D;
    private static double LAST_OFFSET_RANDOM_MAX = 1D;

    public UIFilmPanel panel;
    public UIReplaysOverlayPanel overlay;

    private Map<String, Boolean> expandedGroups = new java.util.HashMap<>();
    private List<Replay> visualList = new ArrayList<>();

    public UIReplayList(Consumer<List<Replay>> callback, UIReplaysOverlayPanel overlay, UIFilmPanel panel) {
        super(callback);

        this.overlay = overlay;
        this.panel = panel;

        this.multi().sorting();
        this.context((menu) -> {
            menu.action(Icons.ADD, UIKeys.SCENE_REPLAYS_CONTEXT_ADD, this::addReplay);

            if (this.isSelected()) {
                menu.action(Icons.COPY, UIKeys.SCENE_REPLAYS_CONTEXT_COPY, this::copyReplay);
            }

            MapType copyReplay = Window.getClipboardMap("_CopyReplay");

            if (copyReplay != null) {
                menu.action(Icons.PASTE, UIKeys.SCENE_REPLAYS_CONTEXT_PASTE, () -> this.pasteReplay(copyReplay));
            }

            int duration = this.panel.getData().camera.calculateDuration();

            if (duration > 0) {
                menu.action(Icons.PLAY, UIKeys.SCENE_REPLAYS_CONTEXT_FROM_CAMERA, () -> this.fromCamera(duration));
            }

            menu.action(Icons.BLOCK, UIKeys.SCENE_REPLAYS_CONTEXT_FROM_MODEL_BLOCK, this::fromModelBlock);

            if (this.isSelected()) {
                boolean isGroup = this.getCurrentFirst().isGroup.get();
                boolean shift = Window.isShiftPressed();
                MapType data = Window.getClipboardMap("_CopyKeyframes");

                if (!isGroup) {
                    menu.action(Icons.ALL_DIRECTIONS, UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS, this::processReplays);
                    menu.action(Icons.TIME, UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME, this::offsetTimeReplays);
                    menu.action(Icons.BLOCK, UIKeys.SCENE_REPLAYS_CONTEXT_RANDOM_SKINS, this::applyRandomSkins);
                }

                menu.action(Icons.FOLDER, UIKeys.SCENE_REPLAYS_CONTEXT_ADD_GROUP, this::addGroup);

                if (!isGroup && data != null) {
                    menu.action(Icons.PASTE, UIKeys.SCENE_REPLAYS_CONTEXT_PASTE_KEYFRAMES,
                            () -> this.pasteToReplays(data));
                }

                if (!isGroup) {
                    menu.action(Icons.DUPE, UIKeys.SCENE_REPLAYS_CONTEXT_DUPE, () -> {
                        if (Window.isShiftPressed() || shift) {
                            this.dupeReplay();
                        } else {
                            UINumberOverlayPanel numberPanel = new UINumberOverlayPanel(
                                    UIKeys.SCENE_REPLAYS_CONTEXT_DUPE, UIKeys.SCENE_REPLAYS_CONTEXT_DUPE_DESCRIPTION,
                                    (n) -> {
                                        for (int i = 0; i < n; i++) {
                                            this.dupeReplay();
                                        }
                                    });

                            numberPanel.value.limit(1).integer();
                            numberPanel.value.setValue(1D);

                            UIOverlay.addOverlay(this.getContext(), numberPanel);
                        }
                    });
                }

                menu.action(Icons.REMOVE, UIKeys.SCENE_REPLAYS_CONTEXT_REMOVE, this::removeReplay);
            }
        });
    }

    @Override
    protected void handleSwap(int from, int to) {
        Replay src = this.list.get(from);
        Replay dest = this.list.get(to);

        // Prevent dragging parent into child
        if (src.isGroup.get()) {
            String srcPath = getReplayPath(src);
            String srcFullPath = srcPath.isEmpty() ? src.uuid.get() : srcPath + "/" + src.uuid.get();

            String destPath = getReplayPath(dest);
            String destFullPath = destPath.isEmpty() ? dest.uuid.get() : destPath + "/" + dest.uuid.get();

            // If dest is strictly inside src or is src itself (circular check)
            if (destFullPath.equals(srcFullPath) || destFullPath.startsWith(srcFullPath + "/") ||
                    dest.group.get().equals(srcFullPath) || dest.group.get().startsWith(srcFullPath + "/")) {
                return;
            }
        }

        // Logic for dropping INTO a group (Reparenting)
        if (dest.isGroup.get()) {
            String destPath = getReplayPath(dest);
            String destGroupPath = destPath.isEmpty() ? dest.uuid.get() : destPath + "/" + dest.uuid.get();
            String srcGroup = src.group.get();

            // If we are dragging onto a group that is NOT our current parent, we reparent
            if (!srcGroup.equals(destGroupPath)) {
                // Calculate insertionAnchor BEFORE modifying src
                // We want to find the last child of dest to append src after it.
                // We must be careful NOT to pick src or any of its children as the anchor.

                Replay insertionAnchor = dest;
                List<Replay> allReplays = this.panel.getData().replays.getAllTyped();

                // Identify src's current full path to exclude its descendants
                String srcPathForCheck = getReplayPath(src);
                String srcFullPathForCheck = srcPathForCheck.isEmpty() ? src.uuid.get()
                        : srcPathForCheck + "/" + src.uuid.get();

                for (Replay r : allReplays) {
                    // Exclude src itself
                    if (r == src)
                        continue;

                    // Exclude descendants of src (using old path)
                    if (src.isGroup.get()) {
                        String g = r.group.get();
                        if (g.equals(srcFullPathForCheck) || g.startsWith(srcFullPathForCheck + "/")) {
                            continue;
                        }
                    }

                    // Check if r is a child of dest
                    String g = r.group.get();
                    if (g.equals(destGroupPath) || g.startsWith(destGroupPath + "/")) {
                        insertionAnchor = r;
                    }
                }

                // Update src parent
                if (src.isGroup.get()) {
                    String oldPath = getReplayPath(src);
                    String oldFullPath = oldPath.isEmpty() ? src.uuid.get() : oldPath + "/" + src.uuid.get();

                    src.group.set(destGroupPath);

                    String newPath = getReplayPath(src);
                    String newFullPath = newPath.isEmpty() ? src.uuid.get() : newPath + "/" + src.uuid.get();

                    this.updateGroupPath(oldFullPath, newFullPath);
                } else {
                    src.group.set(destGroupPath);
                }

                // Move src and children to be inside dest (after last child of dest)
                this.moveReplayAndChildren(src, insertionAnchor, true);

                // Auto-expand the target group
                this.expandedGroups.put(destGroupPath, true);
                this.buildVisualList();
                this.updateFilmEditor();

                return;
            }
        }

        // Standard Reorder (Same Parent or Move to Root)
        String destGroup = dest.group.get();

        if (src.isGroup.get()) {
            String oldPath = getReplayPath(src);
            String oldFullPath = oldPath.isEmpty() ? src.uuid.get() : oldPath + "/" + src.uuid.get();

            src.group.set(destGroup);

            String newPath = getReplayPath(src);
            String newFullPath = newPath.isEmpty() ? src.uuid.get() : newPath + "/" + src.uuid.get();

            if (!oldFullPath.equals(newFullPath)) {
                this.updateGroupPath(oldFullPath, newFullPath);
            }
        } else {
            src.group.set(destGroup);
        }

        this.moveReplayAndChildren(src, dest, from < to);
    }

    private void moveReplayAndChildren(Replay src, Replay dest, boolean insertAfter) {
        Film data = this.panel.getData();
        List<Replay> list = data.replays.getAllTyped();
        List<Replay> toMove = new ArrayList<>();

        // Add src itself
        toMove.add(src);

        // Add descendants
        if (src.isGroup.get()) {
            String srcPath = getReplayPath(src);
            String srcFullPath = srcPath.isEmpty() ? src.uuid.get() : srcPath + "/" + src.uuid.get();

            for (Replay r : list) {
                if (r == src)
                    continue;

                String g = r.group.get();
                if (g.equals(srcFullPath) || g.startsWith(srcFullPath + "/")) {
                    toMove.add(r);
                }
            }
        }

        data.preNotify(IValueListener.FLAG_UNMERGEABLE);

        list.removeAll(toMove);

        int destIndex = list.indexOf(dest);

        if (destIndex != -1) {
            int insertIndex = insertAfter ? destIndex + 1 : destIndex;
            // Clamp index
            insertIndex = Math.max(0, Math.min(insertIndex, list.size()));
            list.addAll(insertIndex, toMove);
        } else {
            list.addAll(toMove);
        }

        data.replays.sync();
        data.postNotify(IValueListener.FLAG_UNMERGEABLE);

        this.buildVisualList();
        this.updateFilmEditor();

        // Restore selection
        int newIndex = this.visualList.indexOf(src);
        if (newIndex != -1) {
            this.setIndex(newIndex);
        }
    }

    private void pasteToReplays(MapType data) {
        UIReplaysEditor replayEditor = this.panel.replayEditor;
        List<Replay> selectedReplays = replayEditor.replays.replays.getCurrent();

        if (data == null) {
            return;
        }

        Map<String, UIKeyframes.PastedKeyframes> parsedKeyframes = UIKeyframes.parseKeyframes(data);

        if (parsedKeyframes.isEmpty()) {
            return;
        }

        UINumberOverlayPanel offsetPanel = new UINumberOverlayPanel(UIKeys.SCENE_REPLAYS_CONTEXT_PASTE_KEYFRAMES_TITLE,
                UIKeys.SCENE_REPLAYS_CONTEXT_PASTE_KEYFRAMES_DESCRIPTION, (n) -> {
                    int tick = this.panel.getCursor();

                    for (Replay replay : selectedReplays) {
                        int randomOffset = (int) (n.intValue() * Math.random());

                        for (Map.Entry<String, UIKeyframes.PastedKeyframes> entry : parsedKeyframes.entrySet()) {
                            String id = entry.getKey();
                            UIKeyframes.PastedKeyframes pastedKeyframes = entry.getValue();
                            KeyframeChannel channel = (KeyframeChannel) replay.keyframes.get(id);

                            if (channel == null || channel.getFactory() != pastedKeyframes.factory) {
                                channel = replay.properties.getOrCreate(replay.form.get(), id);
                            }

                            float min = Integer.MAX_VALUE;

                            for (Keyframe kf : pastedKeyframes.keyframes) {
                                min = Math.min(kf.getTick(), min);
                            }

                            for (Keyframe kf : pastedKeyframes.keyframes) {
                                float finalTick = tick + (kf.getTick() - min) + randomOffset;
                                int index = channel.insert(finalTick, kf.getValue());
                                Keyframe inserted = channel.get(index);

                                inserted.copy(kf);
                                inserted.setTick(finalTick);
                            }

                            channel.sort();
                        }
                    }
                });

        UIOverlay.addOverlay(this.getContext(), offsetPanel);
    }

    private void processReplays() {
        UITextbox expression = new UITextbox((t) -> LAST_PROCESS = t);
        UIStringList properties = new UIStringList(null);
        UIIcon sectionExpression = new UIIcon(Icons.CODE, (b) -> {});
        UIIcon sectionGrid = new UIIcon(Icons.MAZE, (b) -> {});
        UIIcon sectionCircle = new UIIcon(Icons.CIRCLE, (b) -> {});
        UIIcon sectionLine = new UIIcon(Icons.LINE, (b) -> {});
        UIIcon sectionScatter = new UIIcon(Icons.PARTICLE, (b) -> {});
        UITrackpad gridColumns = new UITrackpad((v) -> LAST_PROCESS_GRID_COLUMNS = Math.max(1, v.intValue()));
        UITrackpad gridSpacingX = new UITrackpad((v) -> LAST_PROCESS_GRID_SPACING_X = v.doubleValue());
        UITrackpad gridSpacingZ = new UITrackpad((v) -> LAST_PROCESS_GRID_SPACING_Z = v.doubleValue());
        UITrackpad circleRadius = new UITrackpad((v) -> LAST_PROCESS_CIRCLE_RADIUS = v.doubleValue());
        UITrackpad circleCount = new UITrackpad((v) -> LAST_PROCESS_CIRCLE_COUNT = Math.max(1, v.intValue()));
        UITrackpad circleStartAngle = new UITrackpad((v) -> LAST_PROCESS_CIRCLE_START_ANGLE = v.doubleValue());
        UITrackpad lineDirection = new UITrackpad((v) -> LAST_PROCESS_LINE_DIRECTION = v.doubleValue());
        UITrackpad lineSpacing = new UITrackpad((v) -> LAST_PROCESS_LINE_SPACING = v.doubleValue());
        UITrackpad scatterAreaX = new UITrackpad((v) -> LAST_PROCESS_SCATTER_AREA_X = v.doubleValue());
        UITrackpad scatterAreaZ = new UITrackpad((v) -> LAST_PROCESS_SCATTER_AREA_Z = v.doubleValue());
        UITrackpad scatterSeed = new UITrackpad((v) -> LAST_PROCESS_SCATTER_SEED = v.doubleValue());
        UITrackpad scatterMinSeparation = new UITrackpad((v) -> LAST_PROCESS_SCATTER_MIN_SEPARATION = v.doubleValue());
        UIElement gridControls = UI.column(4,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_GRID_COLUMNS),
            gridColumns,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_GRID_SPACING_X).marginTop(6),
            gridSpacingX,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_GRID_SPACING_Z).marginTop(6),
            gridSpacingZ
        );
        UIElement circleControls = UI.column(4,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_CIRCLE_RADIUS),
            circleRadius,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_CIRCLE_COUNT).marginTop(6),
            circleCount,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_CIRCLE_START_ANGLE).marginTop(6),
            circleStartAngle
        );
        UIElement lineControls = UI.column(4,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_LINE_DIRECTION),
            lineDirection,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_LINE_SPACING).marginTop(6),
            lineSpacing
        );
        UIElement scatterControls = UI.column(4,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SCATTER_AREA_X),
            scatterAreaX,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SCATTER_AREA_Z).marginTop(6),
            scatterAreaZ,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SCATTER_SEED).marginTop(6),
            scatterSeed,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SCATTER_MIN_SEPARATION).marginTop(6),
            scatterMinSeparation
        );
        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_TITLE,
                UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_DESCRIPTION, (b) -> {
                    if (b) {
                        if (LAST_PROCESS_SECTION == 1) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            int count = indices.size();

                            if (count == 0) {
                                return;
                            }

                            int columns = Math.max(1, (int) gridColumns.getValue());
                            int rows = (int) Math.ceil(count / (double) columns);
                            double spacingX = gridSpacingX.getValue();
                            double spacingZ = gridSpacingZ.getValue();

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);

                                int col = order % columns;
                                int row = order / columns;

                                double xOffset = (col - (columns - 1) / 2D) * spacingX;
                                double zOffset = (row - (rows - 1) / 2D) * spacingZ;

                                this.applyOffset(replay, "x", xOffset);
                                this.applyOffset(replay, "z", zOffset);
                            }

                            return;
                        }

                        if (LAST_PROCESS_SECTION == 2) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            int count = indices.size();

                            if (count == 0) {
                                return;
                            }

                            int divisor = Math.max(1, (int) circleCount.getValue());
                            double radius = circleRadius.getValue();
                            double startAngle = circleStartAngle.getValue();

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);

                                double angle = Math.toRadians(startAngle + (order * 360D / divisor));
                                double xOffset = Math.cos(angle) * radius;
                                double zOffset = Math.sin(angle) * radius;

                                this.applyOffset(replay, "x", xOffset);
                                this.applyOffset(replay, "z", zOffset);
                            }

                            return;
                        }

                        if (LAST_PROCESS_SECTION == 3) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            int count = indices.size();

                            if (count == 0) {
                                return;
                            }

                            double direction = lineDirection.getValue();
                            double spacing = lineSpacing.getValue();
                            double angle = Math.toRadians(direction);
                            double stepX = Math.cos(angle) * spacing;
                            double stepZ = Math.sin(angle) * spacing;
                            double center = (count - 1) / 2D;

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);

                                double offset = order - center;
                                double xOffset = stepX * offset;
                                double zOffset = stepZ * offset;

                                this.applyOffset(replay, "x", xOffset);
                                this.applyOffset(replay, "z", zOffset);
                            }

                            return;
                        }

                        if (LAST_PROCESS_SECTION == 4) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            int count = indices.size();

                            if (count == 0) {
                                return;
                            }

                            double areaX = scatterAreaX.getValue();
                            double areaZ = scatterAreaZ.getValue();
                            double minSep = scatterMinSeparation.getValue();
                            double minSepSq = minSep * minSep;
                            long seed = (long) Math.round(scatterSeed.getValue());

                            java.util.Random random = new java.util.Random(seed);
                            List<double[]> placed = new ArrayList<>();

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);

                                double xOffset = 0D;
                                double zOffset = 0D;
                                boolean accepted = false;

                                for (int attempt = 0; attempt < 200; attempt++) {
                                    double candidateX = (random.nextDouble() - 0.5D) * areaX;
                                    double candidateZ = (random.nextDouble() - 0.5D) * areaZ;

                                    boolean ok = true;

                                    if (minSep > 0D) {
                                        for (double[] point : placed) {
                                            double dx = candidateX - point[0];
                                            double dz = candidateZ - point[1];

                                            if (dx * dx + dz * dz < minSepSq) {
                                                ok = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (ok) {
                                        xOffset = candidateX;
                                        zOffset = candidateZ;
                                        accepted = true;
                                        break;
                                    }
                                }

                                if (!accepted) {
                                    xOffset = (random.nextDouble() - 0.5D) * areaX;
                                    zOffset = (random.nextDouble() - 0.5D) * areaZ;
                                }

                                placed.add(new double[] { xOffset, zOffset });

                                this.applyOffset(replay, "x", xOffset);
                                this.applyOffset(replay, "z", zOffset);
                            }

                            return;
                        }


                        MathBuilder builder = new MathBuilder();
                        int min = Integer.MAX_VALUE;

                        builder.register("i");
                        builder.register("o");
                        builder.register("v");
                        builder.register("ki");

                        IExpression parse;

                        try {
                            parse = builder.parse(expression.getText());
                        } catch (Exception e) {
                            return;
                        }

                        LAST_PROCESS_PROPERTIES = new ArrayList<>(properties.getCurrent());

                        for (int index : this.current) {
                            min = Math.min(min, index);
                        }

                        for (int index : this.current) {
                            Replay replay = this.list.get(index);

                            builder.variables.get("i").set(index);
                            builder.variables.get("o").set(index - min);

                            for (String s : properties.getCurrent()) {
                                KeyframeChannel channel = (KeyframeChannel) replay.keyframes.get(s);
                                List keyframes = channel.getKeyframes();

                                for (int i = 0; i < keyframes.size(); i++) {
                                    Keyframe kf = (Keyframe) keyframes.get(i);

                                    builder.variables.get("v").set(kf.getFactory().getY(kf.getValue()));
                                    builder.variables.get("ki").set(i);

                                    kf.setValue(kf.getFactory().yToValue(parse.doubleValue()), true);
                                }
                            }
                        }
                    }
                });

        for (KeyframeChannel<?> channel : this.getCurrentFirst().keyframes.getChannels()) {
            if (KeyframeFactories.isNumeric(channel.getFactory())) {
                properties.add(channel.getId());
            }
        }

        properties.background().multi().sort();
        properties.relative(expression).y(-5).w(1F).h(16 * 9).anchor(0F, 1F);

        if (!LAST_PROCESS_PROPERTIES.isEmpty()) {
            properties.setCurrentScroll(LAST_PROCESS_PROPERTIES.get(0));
        }

        for (String property : LAST_PROCESS_PROPERTIES) {
            properties.addIndex(properties.getList().indexOf(property));
        }

        expression.setText(LAST_PROCESS);
        expression.tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_EXPRESSION_TOOLTIP);
        expression.relative(panel.confirm).y(-1F, -5).w(1F).h(20);

        sectionExpression.active(LAST_PROCESS_SECTION == 0).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SECTION_EXPRESSION);
        sectionGrid.active(LAST_PROCESS_SECTION == 1).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SECTION_GRID);
        sectionCircle.active(LAST_PROCESS_SECTION == 2).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SECTION_CIRCLE);
        sectionLine.active(LAST_PROCESS_SECTION == 3).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SECTION_LINE);
        sectionScatter.active(LAST_PROCESS_SECTION == 4).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_PROCESS_SECTION_SCATTER);

        sectionExpression.callback = (b) ->
        {
            LAST_PROCESS_SECTION = 0;
            sectionExpression.active(true);
            sectionGrid.active(false);
            sectionCircle.active(false);
            sectionLine.active(false);
            sectionScatter.active(false);
            gridControls.setVisible(false);
            circleControls.setVisible(false);
            lineControls.setVisible(false);
            scatterControls.setVisible(false);
            expression.setVisible(true);
            properties.setVisible(true);
        };

        sectionGrid.callback = (b) ->
        {
            LAST_PROCESS_SECTION = 1;
            sectionExpression.active(false);
            sectionGrid.active(true);
            sectionCircle.active(false);
            sectionLine.active(false);
            sectionScatter.active(false);
            gridControls.setVisible(true);
            circleControls.setVisible(false);
            lineControls.setVisible(false);
            scatterControls.setVisible(false);
            expression.setVisible(false);
            properties.setVisible(false);
        };

        sectionCircle.callback = (b) ->
        {
            LAST_PROCESS_SECTION = 2;
            sectionExpression.active(false);
            sectionGrid.active(false);
            sectionCircle.active(true);
            sectionLine.active(false);
            sectionScatter.active(false);
            gridControls.setVisible(false);
            circleControls.setVisible(true);
            lineControls.setVisible(false);
            scatterControls.setVisible(false);
            expression.setVisible(false);
            properties.setVisible(false);
        };

        sectionLine.callback = (b) ->
        {
            LAST_PROCESS_SECTION = 3;
            sectionExpression.active(false);
            sectionGrid.active(false);
            sectionCircle.active(false);
            sectionLine.active(true);
            sectionScatter.active(false);
            gridControls.setVisible(false);
            circleControls.setVisible(false);
            lineControls.setVisible(true);
            scatterControls.setVisible(false);
            expression.setVisible(false);
            properties.setVisible(false);
        };

        sectionScatter.callback = (b) ->
        {
            LAST_PROCESS_SECTION = 4;
            sectionExpression.active(false);
            sectionGrid.active(false);
            sectionCircle.active(false);
            sectionLine.active(false);
            sectionScatter.active(true);
            gridControls.setVisible(false);
            circleControls.setVisible(false);
            lineControls.setVisible(false);
            scatterControls.setVisible(true);
            expression.setVisible(false);
            properties.setVisible(false);
        };

        gridColumns.limit(1).integer().values(1, 1, 5).setValue(LAST_PROCESS_GRID_COLUMNS);
        gridSpacingX.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_GRID_SPACING_X);
        gridSpacingZ.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_GRID_SPACING_Z);

        circleRadius.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_CIRCLE_RADIUS);
        circleCount.limit(1).integer().values(1, 1, 5).setValue(LAST_PROCESS_CIRCLE_COUNT);
        circleStartAngle.values(1D, 0.1D, 10D).setValue(LAST_PROCESS_CIRCLE_START_ANGLE);

        lineDirection.values(1D, 0.1D, 10D).setValue(LAST_PROCESS_LINE_DIRECTION);
        lineSpacing.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_LINE_SPACING);

        scatterAreaX.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_SCATTER_AREA_X);
        scatterAreaZ.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_SCATTER_AREA_Z);
        scatterSeed.values(1D, 0.1D, 10D).setValue(LAST_PROCESS_SCATTER_SEED);
        scatterMinSeparation.values(0.1D, 0.01D, 1D).setValue(LAST_PROCESS_SCATTER_MIN_SEPARATION);

        gridControls.relative(panel.content).x(6).y(70).w(1F, -12).h(1F, -110);
        gridControls.setVisible(LAST_PROCESS_SECTION == 1);

        circleControls.relative(panel.content).x(6).y(70).w(1F, -12).h(1F, -110);
        circleControls.setVisible(LAST_PROCESS_SECTION == 2);

        lineControls.relative(panel.content).x(6).y(70).w(1F, -12).h(1F, -110);
        lineControls.setVisible(LAST_PROCESS_SECTION == 3);

        scatterControls.relative(panel.content).x(6).y(70).w(1F, -12).h(1F, -110);
        scatterControls.setVisible(LAST_PROCESS_SECTION == 4);

        expression.setVisible(LAST_PROCESS_SECTION == 0);
        properties.setVisible(LAST_PROCESS_SECTION == 0);

        panel.confirm.w(1F, -10);
        panel.content.add(gridControls, circleControls, lineControls, scatterControls, expression, properties);
        panel.icons.add(sectionExpression, sectionGrid, sectionCircle, sectionLine, sectionScatter);

        UIOverlay.addOverlay(this.getContext(), panel, 240, 300);
    }

    private void applyOffset(Replay replay, String property, double offset)
    {
        BaseValue value = replay.keyframes.get(property);

        if (!(value instanceof KeyframeChannel<?> channel))
        {
            return;
        }

        @SuppressWarnings("rawtypes")
        KeyframeChannel rawChannel = (KeyframeChannel) channel;
        @SuppressWarnings("rawtypes")
        List<Keyframe> keyframes = rawChannel.getKeyframes();

        for (int i = 0; i < keyframes.size(); i++)
        {
            Keyframe kf = keyframes.get(i);
            double currentValue = rawChannel.getFactory().getY(kf.getValue());

            kf.setValue(rawChannel.getFactory().yToValue(currentValue + offset), true);
        }
    }

    private void offsetTimeReplays() {
        UITextbox tick = new UITextbox((t) -> LAST_OFFSET = t);
        UIIcon sectionExpression = new UIIcon(Icons.CODE, (b) -> {});
        UIIcon sectionStagger = new UIIcon(Icons.TIME, (b) -> {});
        UIIcon sectionAlternating = new UIIcon(Icons.EXCHANGE, (b) -> {});
        UIIcon sectionRandom = new UIIcon(Icons.REFRESH, (b) -> {});
        UITrackpad staggerStep = new UITrackpad((v) -> LAST_OFFSET_STEP = v.doubleValue());
        UITrackpad randomSeed = new UITrackpad((v) -> LAST_OFFSET_RANDOM_SEED = v.doubleValue());
        UITrackpad randomMin = new UITrackpad((v) -> LAST_OFFSET_RANDOM_MIN = v.doubleValue());
        UITrackpad randomMax = new UITrackpad((v) -> LAST_OFFSET_RANDOM_MAX = v.doubleValue());
        UIElement staggerControls = UI.column(4,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_STEP),
            staggerStep
        );
        UIElement randomControls = UI.column(4,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_SEED),
            randomSeed,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_RANDOM_MIN).marginTop(6),
            randomMin,
            UI.label(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_RANDOM_MAX).marginTop(6),
            randomMax
        );
        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_TITLE,
                UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION, (b) -> {
                    if (b) {
                        if (LAST_OFFSET_SECTION == 1) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            double step = staggerStep.getValue();
                            int count = indices.size();

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);
                                float tickv = (float) (order * step);

                                BaseValue.edit(replay, (r) -> r.shift(tickv));
                            }

                            return;
                        }

                        if (LAST_OFFSET_SECTION == 2) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            double step = staggerStep.getValue();
                            int count = indices.size();

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);
                                float tickv = (float) ((order % 2 == 0 ? 1D : -1D) * step);

                                BaseValue.edit(replay, (r) -> r.shift(tickv));
                            }

                            return;
                        }

                        if (LAST_OFFSET_SECTION == 3) {
                            List<Integer> indices = new ArrayList<>(this.current);

                            Collections.sort(indices);

                            double seed = randomSeed.getValue();
                            double min = randomMin.getValue();
                            double max = randomMax.getValue();
                            double start = Math.min(min, max);
                            double end = Math.max(min, max);
                            java.util.Random random = new java.util.Random((long) Math.round(seed));
                            int count = indices.size();

                            for (int order = 0; order < count; order++) {
                                int index = indices.get(order);
                                Replay replay = this.list.get(index);
                                float tickv = (float) (start + (end - start) * random.nextDouble());

                                BaseValue.edit(replay, (r) -> r.shift(tickv));
                            }

                            return;
                        }

                        MathBuilder builder = new MathBuilder();
                        int min = Integer.MAX_VALUE;

                        builder.register("i");
                        builder.register("o");

                        IExpression parse = null;

                        try {
                            parse = builder.parse(tick.getText());
                        } catch (Exception e) {
                        }

                        for (int index : this.current) {
                            min = Math.min(min, index);
                        }

                        for (int index : this.current) {
                            Replay replay = this.list.get(index);

                            builder.variables.get("i").set(index);
                            builder.variables.get("o").set(index - min);

                            float tickv = parse == null ? 0F : (float) parse.doubleValue();

                            BaseValue.edit(replay, (r) -> r.shift(tickv));
                        }
                    }
                });

        tick.setText(LAST_OFFSET);
        tick.tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_EXPRESSION_TOOLTIP);
        tick.relative(panel.confirm).y(-1F, -5).w(1F).h(20);

        sectionExpression.active(LAST_OFFSET_SECTION == 0).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_SECTION_EXPRESSION);
        sectionStagger.active(LAST_OFFSET_SECTION == 1).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_SECTION_STAGGER);
        sectionAlternating.active(LAST_OFFSET_SECTION == 2).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_SECTION_ALTERNATING);
        sectionRandom.active(LAST_OFFSET_SECTION == 3).tooltip(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_SECTION_RANDOM);

        sectionExpression.callback = (b) ->
        {
            LAST_OFFSET_SECTION = 0;
            sectionExpression.active(true);
            sectionStagger.active(false);
            sectionAlternating.active(false);
            sectionRandom.active(false);
            staggerControls.setVisible(false);
            randomControls.setVisible(false);
            tick.setVisible(true);
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_EXPRESSION);
        };

        sectionStagger.callback = (b) ->
        {
            LAST_OFFSET_SECTION = 1;
            sectionExpression.active(false);
            sectionStagger.active(true);
            sectionAlternating.active(false);
            sectionRandom.active(false);
            staggerControls.setVisible(true);
            randomControls.setVisible(false);
            tick.setVisible(false);
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_STAGGER);
        };

        sectionAlternating.callback = (b) ->
        {
            LAST_OFFSET_SECTION = 2;
            sectionExpression.active(false);
            sectionStagger.active(false);
            sectionAlternating.active(true);
            sectionRandom.active(false);
            staggerControls.setVisible(true);
            randomControls.setVisible(false);
            tick.setVisible(false);
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_ALTERNATING);
        };

        sectionRandom.callback = (b) ->
        {
            LAST_OFFSET_SECTION = 3;
            sectionExpression.active(false);
            sectionStagger.active(false);
            sectionAlternating.active(false);
            sectionRandom.active(true);
            staggerControls.setVisible(false);
            randomControls.setVisible(true);
            tick.setVisible(false);
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_RANDOM);
        };

        staggerStep.values(1D, 0.1D, 10D).setValue(LAST_OFFSET_STEP);
        randomSeed.values(1D, 0.1D, 10D).setValue(LAST_OFFSET_RANDOM_SEED);
        randomMin.values(1D, 0.1D, 10D).setValue(LAST_OFFSET_RANDOM_MIN);
        randomMax.values(1D, 0.1D, 10D).setValue(LAST_OFFSET_RANDOM_MAX);
        staggerControls.relative(panel.confirm).x(6).y(-1F, -58).w(1F, -12).h(44);
        staggerControls.setVisible(LAST_OFFSET_SECTION == 1 || LAST_OFFSET_SECTION == 2);
        randomControls.relative(panel.content).x(6).y(70).w(1F, -12).h(1F, -130);
        randomControls.setVisible(LAST_OFFSET_SECTION == 3);
        tick.setVisible(LAST_OFFSET_SECTION == 0);

        if (LAST_OFFSET_SECTION == 1)
        {
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_STAGGER);
        }
        else if (LAST_OFFSET_SECTION == 2)
        {
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_ALTERNATING);
        }
        else if (LAST_OFFSET_SECTION == 3)
        {
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_RANDOM);
        }
        else
        {
            panel.setMessage(UIKeys.SCENE_REPLAYS_CONTEXT_OFFSET_TIME_DESCRIPTION_EXPRESSION);
        }

        panel.confirm.w(1F, -10);
        panel.content.add(staggerControls, randomControls, tick);
        panel.icons.add(sectionExpression, sectionStagger, sectionAlternating, sectionRandom);

        UIOverlay.addOverlay(this.getContext(), panel, 240, 240);
    }

    private void copyReplay() {
        MapType replays = new MapType();
        ListType replayList = new ListType();

        replays.put("replays", replayList);

        for (Replay replay : this.getCurrent()) {
            replayList.add(replay.toData());
        }

        Window.setClipboard(replays, "_CopyReplay");
    }

    private void pasteReplay(MapType data) {
        Film film = this.panel.getData();
        ListType replays = data.getList("replays");
        Replay last = null;

        for (BaseType replayType : replays) {
            Replay replay = film.replays.addReplay();

            BaseValue.edit(replay, (r) -> r.fromData(replayType));
            replay.uuid.set(java.util.UUID.randomUUID().toString());

            last = replay;
        }

        if (last != null) {
            this.buildVisualList();
            this.setCurrentDirect(last);
            this.panel.replayEditor.setReplay(last);
            this.updateFilmEditor();
        }
    }

    public void openFormEditor(ValueForm form, boolean editing, Consumer<Form> consumer) {
        UIElement target = this.panel;

        if (this.getRoot() != null) {
            target = this.getParentContainer();
        }

        UIFormPalette palette = UIFormPalette.open(target, editing, form.get(), (f) -> {
            for (Replay replay : this.getCurrent()) {
                replay.form.set(FormUtils.copy(f));
            }

            this.updateFilmEditor();

            if (consumer != null) {
                consumer.accept(f);
            } else {
                this.overlay.pickEdit.setForm(f);
            }
        });

        palette.updatable();
    }

    private void addReplay() {
        World world = MinecraftClient.getInstance().world;
        Camera camera = this.panel.getCamera();

        BlockHitResult blockHitResult = RayTracing.rayTrace(world, camera, 64F);
        Vec3d p = blockHitResult.getPos();
        Vector3d position = new Vector3d(p.x, p.y, p.z);

        if (blockHitResult.getType() == HitResult.Type.MISS) {
            position.set(camera.getLookDirection()).mul(5F).add(camera.position);
        }

        this.addReplay(position, camera.rotation.x, camera.rotation.y + MathUtils.PI);
    }

    private void fromCamera(int duration) {
        Position position = new Position();
        Clips camera = this.panel.getData().camera;
        CameraClipContext context = new CameraClipContext();

        Film film = this.panel.getData();
        Replay replay = film.replays.addReplay();

        context.clips = camera;

        for (int i = 0; i < duration; i++) {
            context.clipData.clear();
            context.setup(i, 0F);

            for (Clip clip : context.clips.getClips(i)) {
                context.apply(clip, position);
            }

            context.currentLayer = 0;

            float yaw = position.angle.yaw - 180;

            replay.keyframes.x.insert(i, position.point.x);
            replay.keyframes.y.insert(i, position.point.y);
            replay.keyframes.z.insert(i, position.point.z);
            replay.keyframes.yaw.insert(i, (double) yaw);
            replay.keyframes.headYaw.insert(i, (double) yaw);
            replay.keyframes.bodyYaw.insert(i, (double) yaw);
            replay.keyframes.pitch.insert(i, (double) position.angle.pitch);
        }

        this.buildVisualList();
        this.setCurrentDirect(replay);
        this.panel.replayEditor.setReplay(replay);
        this.updateFilmEditor();

        this.openFormEditor(replay.form, false, null);
    }

    private void fromModelBlock() {
        ArrayList<ModelBlockEntity> modelBlocks = new ArrayList<>(BBSRendering.capturedModelBlocks);
        UISearchList<String> search = new UISearchList<>(new UIStringList(null));
        UIList<String> list = search.list;

        list.multi();

        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(UIKeys.SCENE_REPLAYS_CONTEXT_FROM_MODEL_BLOCK_TITLE,
                UIKeys.SCENE_REPLAYS_CONTEXT_FROM_MODEL_BLOCK_DESCRIPTION, (b) -> {
                    if (b) {
                        List<String> selected = list.getCurrent();

                        if (selected.isEmpty()) {
                            int index = list.getIndex();
                            ModelBlockEntity modelBlock = CollectionUtils.getSafe(modelBlocks, index);

                            if (modelBlock != null) {
                                this.fromModelBlock(modelBlock);
                            }
                        } else {
                            for (String name : selected) {
                                int index = list.getList().indexOf(name);
                                ModelBlockEntity modelBlock = CollectionUtils.getSafe(modelBlocks, index);

                                if (modelBlock != null) {
                                    this.fromModelBlock(modelBlock);
                                }
                            }
                        }
                    }
                });

        modelBlocks.sort(Comparator.comparing(ModelBlockEntity::getName));

        for (ModelBlockEntity modelBlock : modelBlocks) {
            list.add(modelBlock.getName());
        }

        list.background();
        search.relative(panel.confirm).y(-5).w(1F).h(16 * 9 + 20).anchor(0F, 1F);

        panel.confirm.w(1F, -10);
        panel.content.add(search);

        UIOverlay.addOverlay(this.getContext(), panel, 240, 300);
    }

    private void fromModelBlock(ModelBlockEntity modelBlock) {
        Film film = this.panel.getData();
        Replay replay = film.replays.addReplay();
        BlockPos blockPos = modelBlock.getPos();
        ModelProperties properties = modelBlock.getProperties();
        Transform transform = properties.getTransform().copy();
        double x = blockPos.getX() + transform.translate.x + 0.5D;
        double y = blockPos.getY() + transform.translate.y;
        double z = blockPos.getZ() + transform.translate.z + 0.5D;

        transform.translate.set(0, 0, 0);

        replay.shadow.set(properties.isShadow());
        replay.form.set(FormUtils.copy(properties.getForm()));
        replay.keyframes.x.insert(0, x);
        replay.keyframes.y.insert(0, y);
        replay.keyframes.z.insert(0, z);

        if (!transform.isDefault()) {
            if (transform.rotate.x == 0 && transform.rotate.z == 0 &&
                    transform.rotate2.x == 0 && transform.rotate2.y == 0 && transform.rotate2.z == 0 &&
                    transform.scale.x == 1 && transform.scale.y == 1 && transform.scale.z == 1) {
                double yaw = -Math.toDegrees(transform.rotate.y);

                replay.keyframes.yaw.insert(0, yaw);
                replay.keyframes.headYaw.insert(0, yaw);
                replay.keyframes.bodyYaw.insert(0, yaw);
            } else {
                AnchorForm form = new AnchorForm();
                BodyPart part = new BodyPart("");

                part.setForm(replay.form.get());
                form.transform.set(transform);
                form.parts.addBodyPart(part);

                replay.form.set(form);
            }
        }

        this.buildVisualList();
        this.setCurrentDirect(replay);
        this.panel.replayEditor.setReplay(replay);
        this.updateFilmEditor();
    }

    public void addReplay(Vector3d position, float pitch, float yaw) {
        Film film = this.panel.getData();
        Replay replay = film.replays.addReplay();

        replay.keyframes.x.insert(0, position.x);
        replay.keyframes.y.insert(0, position.y);
        replay.keyframes.z.insert(0, position.z);

        replay.keyframes.pitch.insert(0, (double) pitch);
        replay.keyframes.yaw.insert(0, (double) yaw);
        replay.keyframes.headYaw.insert(0, (double) yaw);
        replay.keyframes.bodyYaw.insert(0, (double) yaw);

        this.buildVisualList();
        this.setCurrentDirect(replay);
        this.panel.replayEditor.setReplay(replay);
        this.updateFilmEditor();

        this.openFormEditor(replay.form, false, null);
    }

    private void updateFilmEditor() {
        this.panel.getController().createEntities();
        this.panel.replayEditor.updateChannelsList();
    }

    private void dupeReplay() {
        if (this.isDeselected()) {
            return;
        }

        Replay last = null;

        for (Replay replay : this.getCurrent()) {
            Film film = this.panel.getData();
            Replay newReplay = film.replays.addReplay();

            newReplay.copy(replay);
            newReplay.uuid.set(java.util.UUID.randomUUID().toString());

            last = newReplay;
        }

        if (last != null) {
            this.buildVisualList();
            this.setCurrentDirect(last);
            this.panel.replayEditor.setReplay(last);
            this.updateFilmEditor();
        }
    }

    private void applyRandomSkins() {
        if (this.isDeselected()) {
            return;
        }

        UIFolderPickerOverlayPanel panel = new UIFolderPickerOverlayPanel(
                UIKeys.SCENE_REPLAYS_CONTEXT_RANDOM_SKINS,
                IKey.constant("Select the folder containing PNG skin files:"),
                (folder) -> this.processRandomSkins(folder));

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void processRandomSkins(File skinsFolder) {
        if (skinsFolder == null) {
            return;
        }

        if (!skinsFolder.exists() || !skinsFolder.isDirectory()) {
            UIOverlay.addOverlay(this.getContext(),
                    new UIMessageOverlayPanel(UIKeys.GENERAL_ERROR,
                            IKey.constant("The specified folder does not exist or is not a directory.")));
            return;
        }

        // Get all PNG files from the folder
        List<File> skinFiles = new ArrayList<>();
        File[] files = skinsFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                    skinFiles.add(file);
                }
            }
        }

        if (skinFiles.isEmpty()) {
            UIOverlay.addOverlay(this.getContext(),
                    new UIMessageOverlayPanel(UIKeys.GENERAL_ERROR,
                            IKey.constant("No PNG files found in the specified folder.")));
            return;
        }

        // Shuffle the skins for random assignment
        Collections.shuffle(skinFiles);

        // Get selected replays
        List<Replay> selectedReplays = this.getCurrent();

        if (selectedReplays.isEmpty()) {
            return;
        }

        // Apply skins to replays
        int skinIndex = 0;
        int successCount = 0;

        for (Replay replay : selectedReplays) {
            File skinFile = skinFiles.get(skinIndex % skinFiles.size());

            // Create a Link using the AssetProvider
            Link skinLink = BBSMod.getProvider().getLink(skinFile);

            if (skinLink == null) {
                // If the file is not in the assets folder, skip it
                skinIndex++;
                continue;
            }

            // Get the form and set the texture
            ValueForm formValue = replay.form;
            if (formValue != null && formValue.get() != null) {
                Form form = formValue.get();

                if (form instanceof MobForm) {
                    ((MobForm) form).texture.set(skinLink);
                    successCount++;
                } else if (form instanceof ModelForm) {
                    ((ModelForm) form).texture.set(skinLink);
                    successCount++;
                }
            }

            skinIndex++;
        }

        // Update UI
        this.update();
        this.updateFilmEditor();

        if (successCount == 0) {
            UIOverlay.addOverlay(this.getContext(),
                    new UIMessageOverlayPanel(UIKeys.GENERAL_ERROR,
                            IKey.constant(
                                    "The skins folder must be inside the BBS assets folder. For example: config/bbs/assets/models/!Skins/")));
        }
    }

    private void removeReplay() {
        if (this.isDeselected()) {
            return;
        }

        Film film = this.panel.getData();
        int index = this.getIndex();

        for (Replay replay : this.getCurrent()) {
            if (replay.isGroup.get()) {
                this.reparentChildren(replay);
            }

            film.replays.remove(replay);
        }

        int size = this.list.size();
        index = MathUtils.clamp(index, 0, size - 1);

        this.buildVisualList();
        size = this.list.size();
        this.panel.replayEditor.setReplay(size == 0 ? null : CollectionUtils.getSafe(this.list, index));
        this.updateFilmEditor();
    }

    private void reparentChildren(Replay groupToDelete) {
        Film data = this.panel.getData();
        List<Replay> allReplays = data.replays.getAllTyped();

        String targetPath = getReplayPath(groupToDelete);
        String targetID = groupToDelete.uuid.get();
        String childPrefix = targetPath.isEmpty() ? targetID : targetPath + "/" + targetID;
        String newParentPath = targetPath;

        for (Replay r : allReplays) {
            if (r == groupToDelete)
                continue;

            String g = r.group.get();

            if (g.equals(childPrefix) || g.startsWith(childPrefix + "/")) {
                String suffix = g.substring(childPrefix.length());
                String newPath;

                if (newParentPath.isEmpty()) {
                    newPath = suffix.startsWith("/") ? suffix.substring(1) : suffix;
                } else {
                    newPath = newParentPath + suffix;
                }

                r.group.set(newPath);
            }
        }
    }

    @Override
    public void render(UIContext context) {
        super.render(context);
    }

    @Override
    protected String elementToString(UIContext context, int i, Replay element) {
        return context.batcher.getFont().limitToWidth(element.getName(), this.area.w - 20);
    }

    @Override
    protected void renderElementPart(UIContext context, Replay element, int i, int x, int y, boolean hover,
            boolean selected) {
        int depth = getReplayDepth(element);
        int indent = depth * 10;
        int textX = x + indent;

        if (element.isGroup.get()) {
            String path = getReplayPath(element);
            String myPath = path.isEmpty() ? element.uuid.get() : path + "/" + element.uuid.get();
            boolean expanded = this.expandedGroups.getOrDefault(myPath, true);
            Icon icon = expanded ? Icons.ARROW_DOWN : Icons.ARROW_RIGHT;

            context.batcher.icon(icon, textX, y + 2);
            textX += 12;
        }

        if (element.enabled.get()) {
            super.renderElementPart(context, element, i, textX, y, hover, selected);
        } else {
            context.batcher.textShadow(this.elementToString(context, i, element), textX + 4,
                    y + (this.scroll.scrollItemSize - context.batcher.getFont().getHeight()) / 2,
                    hover ? Colors.mulRGB(Colors.HIGHLIGHT, 0.75F) : Colors.GRAY);
        }

        Form form = element.form.get();

        if (form != null) {
            x += this.area.w - 30;

            context.batcher.clip(x, y, 40, 20, context);

            y -= 10;

            FormUtilsClient.renderUI(form, context, x, y, x + 40, y + 40);

            context.batcher.unclip(context);

            if (element.fp.get()) {
                context.batcher.outlinedIcon(Icons.ARROW_UP, x, y + 20, 0.5F, 0.5F);
            }
        }
    }

    private void addGroup() {
        Film film = this.panel.getData();
        Replay group = new Replay("replay");

        group.uuid.set(java.util.UUID.randomUUID().toString());
        group.isGroup.set(true);
        group.label.set("New Group");

        List<Replay> selected = this.getCurrent();

        if (!selected.isEmpty()) {
            List<Replay> list = film.replays.getAllTyped();
            Replay first = selected.get(0);

            int insertionIndex = list.size();

            for (Replay r : selected) {
                int index = list.indexOf(r);

                if (index != -1 && index < insertionIndex) {
                    insertionIndex = index;
                }
            }

            String parentPath = first.group.get();

            group.group.set(parentPath);

            String newGroupPath = parentPath.isEmpty() ? group.uuid.get() : parentPath + "/" + group.uuid.get();

            list.removeAll(selected);

            for (Replay r : selected) {
                r.group.set(newGroupPath);
            }

            if (insertionIndex > list.size()) {
                insertionIndex = list.size();
            }

            list.add(insertionIndex, group);
            list.addAll(insertionIndex + 1, selected);

            this.expandedGroups.put(newGroupPath, true);
        } else {
            film.replays.add(group);
        }

        film.replays.sync();

        this.buildVisualList();
        this.updateFilmEditor();
    }

    public void buildVisualList() {
        if (this.panel == null || this.panel.getData() == null)
            return;

        List<Replay> selected = new ArrayList<>();

        if (this.list != null && !this.list.isEmpty()) {
            selected = this.getCurrent();
        }

        List<Replay> all = this.panel.getData().replays.getList();

        this.visualList.clear();

        for (Replay r : all) {
            String path = getReplayPath(r);

            if (path.isEmpty() || isPathExpanded(path)) {
                this.visualList.add(r);
            }
        }

        this.setList(this.visualList);
        this.current.clear();

        for (Replay r : selected) {
            int index = this.visualList.indexOf(r);

            if (index != -1) {
                this.current.add(index);
            }
        }
    }

    private boolean isPathExpanded(String path) {
        String[] parts = path.split("/");
        String current = "";

        for (String part : parts) {
            current = current.isEmpty() ? part : current + "/" + part;

            if (!this.expandedGroups.getOrDefault(current, true)) {
                return false;
            }
        }

        return true;
    }

    public void updateGroupPath(String oldFullPath, String newFullPath) {
        Film film = this.panel.getData();
        List<Replay> all = film.replays.getList();
        boolean changed = false;

        // Update expanded state key
        if (this.expandedGroups.containsKey(oldFullPath)) {
            this.expandedGroups.put(newFullPath, this.expandedGroups.remove(oldFullPath));
        }

        // Update children paths
        for (Replay r : all) {
            String group = r.group.get();

            if (group.equals(oldFullPath) || group.startsWith(oldFullPath + "/")) {
                String suffix = group.substring(oldFullPath.length());
                r.group.set(newFullPath + suffix);
                changed = true;
            }
        }

        if (changed) {
            film.replays.sync();
            this.buildVisualList();
            this.updateFilmEditor();
        }
    }

    public String getReplayPath(Replay r) {
        return r.group.get();
    }

    private int getReplayDepth(Replay r) {
        String path = getReplayPath(r);
        return path.isEmpty() ? 0 : path.split("/").length;
    }

    @Override
    public boolean subMouseClicked(UIContext context) {
        if (context.mouseButton == 0) {
            int index = this.scroll.getIndex(context.mouseX, context.mouseY);

            if (this.exists(index)) {
                Replay r = this.list.get(index);
                int depth = getReplayDepth(r);
                int indent = depth * 10;
                int x = this.area.x + indent;

                if (r.isGroup.get() && context.mouseX >= x && context.mouseX < x + 16) {
                    String path = getReplayPath(r);
                    String myPath = path.isEmpty() ? r.uuid.get() : path + "/" + r.uuid.get();

                    boolean expanded = this.expandedGroups.getOrDefault(myPath, true);

                    this.expandedGroups.put(myPath, !expanded);
                    this.buildVisualList();

                    return true;
                }
            }
        }

        return super.subMouseClicked(context);
    }

}
