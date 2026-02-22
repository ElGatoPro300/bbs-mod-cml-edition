package elgatopro300.bbs_cml.ui.model;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.client.BBSShaders;
import elgatopro300.bbs_cml.cubic.ModelInstance;
import elgatopro300.bbs_cml.cubic.model.ModelConfig;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.renderers.FormRenderType;
import elgatopro300.bbs_cml.forms.renderers.FormRenderingContext;
import elgatopro300.bbs_cml.forms.renderers.ModelFormRenderer;
import elgatopro300.bbs_cml.graphics.texture.Texture;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.utils.StencilMap;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIModelRenderer;
import elgatopro300.bbs_cml.ui.utils.Gizmo;
import elgatopro300.bbs_cml.ui.utils.StencilFormFramebuffer;
import elgatopro300.bbs_cml.utils.MatrixStackUtils;
import elgatopro300.bbs_cml.utils.Pair;
import elgatopro300.bbs_cml.utils.colors.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

import elgatopro300.bbs_cml.ui.utils.Gizmo;
import elgatopro300.bbs_cml.ui.framework.UIBaseMenu;
import elgatopro300.bbs_cml.ui.framework.elements.input.UIPropTransform;
import elgatopro300.bbs_cml.forms.renderers.utils.MatrixCache;
import elgatopro300.bbs_cml.forms.renderers.utils.MatrixCacheEntry;

public class UIModelEditorRenderer extends UIModelRenderer
{
    public UIPropTransform transform;

    private ModelForm form = new ModelForm();
    private ModelFormRenderer renderer;
    private ModelConfig config;
    private Consumer<String> callback;
    private String selectedBone;
    private boolean dirty = true;

    private StencilFormFramebuffer stencil = new StencilFormFramebuffer();
    private StencilMap stencilMap = new StencilMap();

    private ModelInstance previewModel;
    private String lastModelId;

    public UIModelEditorRenderer()
    {
        super();
        this.renderer = new ModelFormRenderer(this.form)
        {
            @Override
            public ModelInstance getModel()
            {
                return UIModelEditorRenderer.this.getModel();
            }
        };
    }

    public void setModel(String modelId)
    {
        this.form.model.set(modelId);
    }
    
    public void setConfig(ModelConfig config)
    {
        this.config = config;
    }

    public void setCallback(Consumer<String> callback)
    {
        this.callback = callback;
    }
    
    public void dirty()
    {
        this.dirty = true;
    }

    private void ensureFramebuffer()
    {
        this.stencil.setup(Link.bbs("stencil_form"));
        this.stencil.resizeGUI(this.area.w, this.area.h);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.ensureFramebuffer();
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.stencil.hasPicked())
        {
            Pair<Form, String> picked = this.stencil.getPicked();

            if (picked != null)
            {
                if (picked.a == null)
                {
                    int index = this.stencil.getIndex();
                    
                    if (index >= Gizmo.STENCIL_X && index <= Gizmo.STENCIL_FREE)
                    {
                        Gizmo.INSTANCE.start(index, context.mouseX, context.mouseY, this.transform);
                        return true;
                    }
                }
                else if (this.callback != null)
                {
                    this.callback.accept(picked.b);
                    return true;
                }
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        Gizmo.INSTANCE.stop();
        
        return super.subMouseReleased(context);
    }

    public void setSelectedBone(String bone)
    {
        this.selectedBone = bone;
    }

    public String getSelectedBone()
    {
        return this.selectedBone;
    }

    @Override
    protected void renderUserModel(UIContext context)
    {
        this.updateModel();
        
        FormRenderingContext formContext = new FormRenderingContext()
            .set(FormRenderType.PREVIEW, this.entity, new MatrixStack(), LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, context.getTransition())
            .camera(this.camera)
            .modelRenderer();

        this.renderer.render(formContext);

        /* Render Axes */
        Matrix4f gizmoMatrix = null;

        if (UIBaseMenu.renderAxes && this.selectedBone != null && !this.selectedBone.isEmpty())
        {
            MatrixCache map = this.renderer.collectMatrices(this.entity, context.getTransition());
            MatrixCacheEntry entry = map.get(this.selectedBone);
            
            if (entry != null)
            {
                Matrix4f matrix = entry.origin();
                
                if (matrix == null)
                {
                    matrix = entry.matrix();
                }
                
                if (matrix != null)
                {
                    gizmoMatrix = matrix;

                    MatrixStack stack = new MatrixStack();
                    
                    stack.push();
                    MatrixStackUtils.multiply(stack, matrix);
                    
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    Gizmo.INSTANCE.render(stack);
                    stack.pop();
                    
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
        }

        if (this.area.isInside(context))
        {
            if (this.stencil.getFramebuffer() == null)
            {
                this.ensureFramebuffer();
            }

            // RenderSystem.disableScissor();

            this.stencilMap.setup();
            this.stencil.apply();

            this.renderer.render(formContext.stencilMap(this.stencilMap));

            if (gizmoMatrix != null)
            {
                MatrixStack stack = new MatrixStack();

                stack.push();
                MatrixStackUtils.multiply(stack, gizmoMatrix);

                GL11.glDisable(GL11.GL_DEPTH_TEST);
                Gizmo.INSTANCE.renderStencil(stack, this.stencilMap);
                GL11.glEnable(GL11.GL_DEPTH_TEST);

                stack.pop();
            }

            this.stencil.pickGUI(context, this.area);
            this.stencil.unbind(this.stencilMap);

            // MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

            // RenderSystem.enableScissor(this.area.x, this.area.y, this.area.w, this.area.h);
        }
        else
        {
            this.stencil.clearPicking();
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.stencilMap.hasStencil())
        {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            
            this.stencilMap.render(context);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        if (!this.stencil.hasPicked())
        {
            // MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            
            return;
        }

        Texture texture = this.stencil.getFramebuffer().getMainTexture();
        int index = this.stencil.getIndex();
        int w = texture.width;
        int h = texture.height;

        ShaderProgram previewProgram = BBSShaders.getPickerPreviewProgram();
        GlUniform target = previewProgram.getUniform("Target");

        if (target != null)
        {
            // target.set(index);
        }

        GL11.glEnable(GL11.GL_BLEND);
        context.batcher.texturedBox(BBSShaders::getPickerPreviewProgram, texture.id, Colors.WHITE, this.area.x, this.area.y, this.area.w, this.area.h, 0, h, w, 0, w, h);

        Pair<Form, String> pair = this.stencil.getPicked();

        if (pair != null && pair.a != null && !pair.b.isEmpty())
        {
            String label = pair.a.getFormIdOrName() + " - " + pair.b;

            context.batcher.textCard(label, context.mouseX + 12, context.mouseY + 8);
        }
    }
    
    private void updateModel()
    {
        if (this.config == null)
        {
            return;
        }

        this.form.color.get().set(this.config.color.get());

        if (!this.dirty)
        {
            return;
        }

        this.dirty = false;

        try
        {
            ModelInstance model = this.getModel();

            if (model != null)
            {
                boolean wasProcedural = model.procedural;

                model.applyConfig((MapType) this.config.toData());
                model.texture = this.config.texture.get();
                model.color = this.config.color.get();

                if (wasProcedural != model.procedural)
                {
                    this.renderer.resetAnimator();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private ModelInstance getModel()
    {
        String modelId = this.form.model.get();

        if (modelId.isEmpty())
        {
            this.deletePreview();
            return null;
        }

        if (!modelId.equals(this.lastModelId) || this.previewModel == null)
        {
            ModelInstance globalModel = BBSModClient.getModels().getModel(modelId);

            if (globalModel != null)
            {
                this.deletePreview();

                this.previewModel = new ModelInstance(globalModel.id, globalModel.model, globalModel.animations, globalModel.texture);
                this.previewModel.setup();

                if (this.config != null)
                {
                    try
                    {
                        this.previewModel.applyConfig((MapType) this.config.toData());
                        this.previewModel.texture = this.config.texture.get();
                        this.previewModel.color = this.config.color.get();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                this.lastModelId = modelId;
            }
        }

        return this.previewModel;
    }

    private void deletePreview()
    {
        if (this.previewModel != null)
        {
            this.previewModel.delete();
            this.previewModel = null;
        }

        this.lastModelId = null;
    }
}
