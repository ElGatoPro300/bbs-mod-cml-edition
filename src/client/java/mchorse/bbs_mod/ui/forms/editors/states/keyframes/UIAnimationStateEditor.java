package elgatopro300.bbs_cml.ui.forms.editors.states.keyframes;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.cubic.ModelInstance;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.renderers.ModelFormRenderer;
import elgatopro300.bbs_cml.forms.renderers.utils.MatrixCache;
import elgatopro300.bbs_cml.forms.renderers.utils.MatrixCacheEntry;
import elgatopro300.bbs_cml.forms.states.AnimationState;
import elgatopro300.bbs_cml.graphics.window.Window;
import elgatopro300.bbs_cml.settings.values.base.BaseValueBasic;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditor;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditorUtils;
import elgatopro300.bbs_cml.ui.film.replays.overlays.UIAnimationToPoseOverlayPanel;
import elgatopro300.bbs_cml.ui.film.replays.overlays.UIKeyframeSheetFilterOverlayPanel;
import elgatopro300.bbs_cml.ui.forms.editors.UIFormEditor;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframeEditor;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.graphs.UIKeyframeDopeSheet;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIDraggable;
import elgatopro300.bbs_cml.ui.utils.Gizmo;
import elgatopro300.bbs_cml.ui.utils.StencilFormFramebuffer;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.Pair;
import elgatopro300.bbs_cml.utils.StringUtils;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.joml.Matrices;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeChannel;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UIAnimationStateEditor extends UIElement
{
    public UIKeyframeEditor keyframeEditor;

    public UIFormEditor editor;
    public UIElement editArea;

    private AnimationState state;
    private Set<String> keys = new LinkedHashSet<>();

    public UIAnimationStateEditor(UIFormEditor editor)
    {
        this.editor = editor;

        this.editArea = new UIElement();
        this.editArea.relative(this)
            .x(BBSSettings.editorLayoutSettings.getStateEditorSizeH())
            .wTo(this.area, 1F)
            .h(1F);

        UIDraggable draggable = new UIDraggable((context) ->
        {
            float fx = (context.mouseX - this.area.x) / (float) this.area.w;
            float fy = -(context.mouseY - this.getParent().area.ey()) / (float) this.getParent().area.h;

            BBSSettings.editorLayoutSettings.setStateEditorSizeV(fy);
            BBSSettings.editorLayoutSettings.setStateEditorSizeH(fx);

            this.h(BBSSettings.editorLayoutSettings.getStateEditorSizeV());
            this.editArea.x(BBSSettings.editorLayoutSettings.getStateEditorSizeH());
            this.getParent().resize();
        });

        draggable.reference(() -> new Vector2i(this.editArea.area.x, this.area.y));
        draggable.rendering((context) ->
        {
            int size = 5;
            int x = this.editArea.area.x + 3;
            int y = this.editArea.area.y + 3;

            context.batcher.box(x, y, x + 1, y + size, Colors.WHITE);
            context.batcher.box(x, y - 1, x + size, y, Colors.WHITE);

            x = this.editArea.area.x - 3;
            y = this.editArea.area.y + 3;

            context.batcher.box(x - 1, y, x, y + size, Colors.WHITE);
            context.batcher.box(x - size, y - 1, x, y, Colors.WHITE);
        });

        draggable.hoverOnly().relative(this.editArea).w(40).h(6).anchorX(0.5F);

        this.add(this.editArea, draggable);
    }

    public AnimationState getState()
    {
        return this.state;
    }

    public void setState(AnimationState state)
    {
        UIKeyframes lastEditor = null;

        if (this.keyframeEditor != null)
        {
            lastEditor = this.keyframeEditor.view;

            this.keyframeEditor.removeFromParent();
            this.keyframeEditor = null;
        }

        this.state = state;

        if (this.state == null)
        {
            return;
        }

        List<UIKeyframeSheet> sheets = new ArrayList<>();

        /* Form properties */
        for (String key : FormUtils.collectPropertyPaths(this.editor.form))
        {
            KeyframeChannel property = this.state.properties.getOrCreate(this.editor.form, key);

            if (property != null)
            {
                BaseValueBasic formProperty = FormUtils.getProperty(this.editor.form, key);
                UIKeyframeSheet sheet = new UIKeyframeSheet(UIReplaysEditor.getColor(key), false, property, formProperty);

                sheets.add(sheet.icon(UIReplaysEditor.getIcon(key)));
            }
        }

        this.keys.clear();

        for (UIKeyframeSheet sheet : sheets)
        {
            this.keys.add(StringUtils.fileName(sheet.id));
        }

        sheets.removeIf((v) ->
        {
            if (v.id.equals("anchor"))
            {
                return true;
            }

            for (String s : BBSSettings.disabledSheets.get())
            {
                if (v.id.equals(s) || v.id.endsWith("/" + s))
                {
                    return true;
                }
            }

            return false;
        });

        Object lastForm = null;

        for (UIKeyframeSheet sheet : sheets)
        {
            Object form = sheet.property == null ? null : FormUtils.getForm(sheet.property);

            if (!Objects.equals(lastForm, form))
            {
                sheet.separator = true;
            }

            lastForm = form;
        }

        if (!sheets.isEmpty())
        {
            this.keyframeEditor = new UIKeyframeEditor((consumer) -> new UIAnimationStateKeyframes(this.editor, consumer)).target(this.editArea);
            this.keyframeEditor.relative(this).h(1F).wTo(this.editArea.area);
            this.keyframeEditor.setUndoId("form_animation_state_keyframe_editor");

            /* Reset */
            if (lastEditor != null)
            {
                this.keyframeEditor.view.copyViewport(lastEditor);
            }

            this.keyframeEditor.view.duration(() -> this.state.duration.get());
            this.keyframeEditor.view.context((menu) ->
            {
                if (this.editor.form instanceof ModelForm modelForm)
                {
                    int mouseY = this.getContext().mouseY;
                    UIKeyframeSheet sheet = this.keyframeEditor.view.getGraph().getSheet(mouseY);

                    if (sheet != null && sheet.channel.getFactory() == KeyframeFactories.POSE && sheet.id.equals("pose"))
                    {
                        menu.action(Icons.POSE, UIKeys.FILM_REPLAY_CONTEXT_ANIMATION_TO_KEYFRAMES, () ->
                        {
                            ModelInstance model = ModelFormRenderer.getModel(modelForm);

                            if (model != null)
                            {
                                UIOverlay.addOverlay(this.getContext(), new UIAnimationToPoseOverlayPanel((animationKey, onlyKeyframes, length, step) ->
                                {
                                    int current = this.editor.getCursor();
                                    IEntity entity = this.editor.renderer.getTargetEntity();

                                    UIReplaysEditorUtils.animationToPoseKeyframes(this.keyframeEditor, sheet, modelForm, entity, current, animationKey, onlyKeyframes, length, step);
                                }, modelForm, sheet), 260, 260);
                            }
                        });
                    }
                }

                if (this.keyframeEditor.view.getGraph() instanceof UIKeyframeDopeSheet)
                {
                    menu.action(Icons.FILTER, UIKeys.FILM_REPLAY_FILTER_SHEETS, () ->
                    {
                        UIKeyframeSheetFilterOverlayPanel panel = new UIKeyframeSheetFilterOverlayPanel(BBSSettings.disabledSheets.get(), this.keys);

                        UIOverlay.addOverlay(this.getContext(), panel, 240, 0.9F);

                        panel.onClose((e) ->
                        {
                            this.setState(this.state);
                            BBSSettings.disabledSheets.set(BBSSettings.disabledSheets.get());
                        });
                    });
                }
            });

            for (UIKeyframeSheet sheet : sheets)
            {
                this.keyframeEditor.view.addSheet(sheet);
            }

            this.addAfter(this.editArea, this.keyframeEditor);
        }

        this.resize();

        if (this.keyframeEditor != null && lastEditor == null)
        {
            this.keyframeEditor.view.resetView();
        }
    }

    public boolean clickViewport(UIContext context, StencilFormFramebuffer stencil)
    {
        if (stencil.hasPicked() && this.state != null)
        {
            Pair<Form, String> pair = stencil.getPicked();

            if (pair != null && context.mouseButton < 2)
            {
                if (Gizmo.INSTANCE.start(stencil.getIndex(), context.mouseX, context.mouseY, UIReplaysEditorUtils.getEditableTransform(this.keyframeEditor)))
                {
                    return true;
                }

                if (context.mouseButton == 0)
                {
                    if (Window.isCtrlPressed()) UIReplaysEditorUtils.offerAdjacent(this.getContext(), pair.a, pair.b, (bone) -> this.pickForm(pair.a, bone));
                    else if (Window.isShiftPressed()) UIReplaysEditorUtils.offerHierarchy(this.getContext(), pair.a, pair.b, (bone) -> this.pickForm(pair.a, bone));
                    else this.pickForm(pair.a, pair.b);

                    return true;
                }
                else if (context.mouseButton == 1)
                {
                    this.pickFormProperty(pair.a, pair.b);

                    return true;
                }
            }
        }

        return false;
    }

    public void pickForm(Form form, String bone)
    {
        UIReplaysEditorUtils.pickForm(this.keyframeEditor, this.editor, form, bone);
    }

    public void pickFormProperty(Form form, String bone)
    {
        UIReplaysEditorUtils.pickFormProperty(this.getContext(), this.keyframeEditor, this.editor, form, bone);
    }

    public Matrix4f getOrigin(float transition)
    {
        if (this.keyframeEditor == null)
        {
            return Matrices.EMPTY_4F;
        }

        Pair<String, Boolean> bone = this.keyframeEditor.getBone();

        if (bone == null)
        {
            return Matrices.EMPTY_4F;
        }

        Form root = FormUtils.getRoot(this.editor.form);
        MatrixCache map = FormUtilsClient.getRenderer(root).collectMatrices(this.editor.renderer.getTargetEntity(), transition);
        
        String key = bone.a;
        boolean forceOrigin = key.endsWith("#origin");
        
        if (forceOrigin) key = key.substring(0, key.length() - 7);
        
        MatrixCacheEntry entry = map.get(key);
        
        if (entry == null)
        {
            return Matrices.EMPTY_4F;
        }

        Matrix4f matrix;

        if (forceOrigin)
        {
            matrix = entry.origin();
        }
        else if (bone.b)
        {
            Matrix4f localMatrix = entry.matrix();
            Matrix4f originMatrix = entry.origin();

            if (localMatrix != null && originMatrix != null)
            {
                matrix = new Matrix4f(localMatrix);
                matrix.setTranslation(originMatrix.getTranslation(new org.joml.Vector3f()));
            }
            else
            {
                matrix = localMatrix != null ? localMatrix : originMatrix;
            }
        }
        else
        {
            matrix = entry.origin();
        }

        return matrix == null ? Matrices.EMPTY_4F : matrix;
    }

    @Override
    public void render(UIContext context)
    {
        if (this.keyframeEditor != null)
        {
            this.editArea.area.render(context.batcher, Colors.A75);
        }

        super.render(context);
    }
}
