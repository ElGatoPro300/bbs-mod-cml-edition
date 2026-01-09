package mchorse.bbs_mod.forms.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.client.BBSShaders;
import mchorse.bbs_mod.cubic.render.vao.Attributes;
import mchorse.bbs_mod.cubic.render.vao.IModelVAO;
import mchorse.bbs_mod.cubic.render.vao.ModelVAO;
import mchorse.bbs_mod.cubic.render.vao.ModelVAOData;
import mchorse.bbs_mod.cubic.render.vao.ModelVAORenderer;
import mchorse.bbs_mod.cubic.render.vao.StructureVAOCollector;
import mchorse.bbs_mod.forms.CustomVertexConsumerProvider;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.forms.StructureForm;
import mchorse.bbs_mod.forms.forms.utils.PivotSettings;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.forms.renderers.utils.RecolorVertexConsumer;
import mchorse.bbs_mod.forms.renderers.utils.VirtualBlockRenderView;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.utils.MatrixStackUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.joml.Vectors;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.List;
import java.util.Optional;

/**
 * StructureForm Renderer
 *
 * Implements NBT loading and basic rendering iterating blocks.
 * To minimize files, the NBT loader is integrated here.
 */
public class StructureFormRenderer extends FormRenderer<StructureForm>
{
    private final List<BlockEntry> blocks = new ArrayList<>();
    private final List<BlockEntry> animatedBlocks = new ArrayList<>();
    private final List<BlockEntry> biomeTintedBlocks = new ArrayList<>();
    private final List<BlockEntry> blockEntitiesList = new ArrayList<>();
    private String lastFile = null;
    private BlockPos size = BlockPos.ORIGIN;
    private BlockPos boundsMin = null;
    private BlockPos boundsMax = null;
    private IModelVAO structureVao = null;
    private boolean vaoDirty = true;
    private boolean vaoBuiltWithShaders = false;
    private boolean capturingVAO = false;
    /* Dedicated VAO for picking (includes animated and biome tinted blocks) */
    private IModelVAO structureVaoPicking = null;
    private boolean vaoPickingDirty = true;

    /* Light cache to detect changes and rebuild VAO */
    private boolean lastEmitLight = false;
    private int lastLightIntensity = 0;

    /* Controls whether special blocks should be included during VAO capture */
    private boolean capturingIncludeSpecialBlocks = false;
    private boolean hasTranslucentLayer = false;
    private boolean hasCutoutLayer = false;
    private boolean hasAnimatedLayer = false;
    private boolean hasBiomeTintedLayer = false;
    private boolean hasBlockEntityLayer = false;
    private VirtualBlockRenderView.Entry[] entriesCache = null;
    private VirtualBlockRenderView cachedView = null;

    public StructureFormRenderer(StructureForm form)
    {
        super(form);
    }

    @Override
    public void renderInUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            return;
        }

        /* Ensure the current UI batch is flushed before drawing 3D */
        context.batcher.getContext().draw();

        this.ensureLoaded();

        MatrixStack matrices = context.batcher.getContext().getMatrices();

        Matrix4f uiMatrix = ModelFormRenderer.getUIMatrix(context, x1, y1, x2, y2);

        matrices.push();
        MatrixStackUtils.multiply(matrices, uiMatrix);
        /* To draw 3D content inside UI, use standard depth test */
        /* and restore it at the end to avoid affecting other panels. */
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        /* Autoscale: adjust so the structure fits in the cell without clipping */
        float cellW = x2 - x1;
        float cellH = y2 - y1;
        float baseScale = cellH / 2.5F; /* Same as in ModelFormRenderer#getUIMatrix */

        int wUnits = 1, hUnits = 1, dUnits = 1;

        if (this.boundsMin != null && this.boundsMax != null)
        {
            wUnits = Math.max(1, this.boundsMax.getX() - this.boundsMin.getX() + 1);
            hUnits = Math.max(1, this.boundsMax.getY() - this.boundsMin.getY() + 1);
            dUnits = Math.max(1, this.boundsMax.getZ() - this.boundsMin.getZ() + 1);
        }
        else
        {
            wUnits = Math.max(1, this.size.getX());
            hUnits = Math.max(1, this.size.getY());
            dUnits = Math.max(1, this.size.getZ());
        }

        int maxUnits = Math.max(wUnits, Math.max(hUnits, dUnits));
        float targetPixels = Math.min(cellW, cellH) * 0.9F; /* 10% margin */
        float auto = maxUnits > 0 ? targetPixels / (baseScale * maxUnits) : 1F;
        /* Do not exceed user defined scale; only reduce if necessary */
        float finalScale = this.form.uiScale.get() * Math.min(1F, auto);

        matrices.scale(finalScale, finalScale, finalScale);

        matrices.peek().getNormalMatrix().getScale(Vectors.EMPTY_3F);
        matrices.peek().getNormalMatrix().scale(1F / Vectors.EMPTY_3F.x, -1F / Vectors.EMPTY_3F.y, 1F / Vectors.EMPTY_3F.z);

        boolean optimize = BBSSettings.structureOptimization.get();
        /* If structure light is enabled, force BufferBuilder path */
        /* so dynamic light calculation via VirtualBlockRenderView applies. */
        /* This prevents VAO (simpler lighting) from ignoring the light panel. */
        /*
           MODIFICATION: Allow optimization (VAO) even with lights enabled,
           as LightmapStructureVAOCollector now handles virtual light.

           Original rendering logic is kept if optimization is globally disabled.
        */

        if (!optimize)
        {
            /* BufferBuilder mode: better lighting, worse performance */
            boolean shaders = this.isShadersActive();
            VertexConsumerProvider consumers = shaders
                ? MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
                : VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

            try
            {
                FormRenderingContext uiContext = new FormRenderingContext()
                    .set(FormRenderType.PREVIEW, null, matrices, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0F);
                
                this.renderStructureCulledWorld(uiContext, matrices, consumers, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, shaders, null);
                
                if (consumers instanceof VertexConsumerProvider.Immediate immediate)
                {
                    immediate.draw();
                }
            }
            catch (Throwable e)
            {
            }
        }
        else
        {
            /* Prepare VAO if necessary and draw with shader compatible with animations */
            if (this.structureVao == null || this.vaoDirty)
            {
                this.buildStructureVAO();
            }

            if (this.structureVao != null)
            {
                Color tint = this.form.color.get();
                GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
                
                gameRenderer.getLightmapTextureManager().enable();
                gameRenderer.getOverlayTexture().setupOverlayColor();

                /* Return to own model shader in vanilla to ensure VAO compatibility */
                ShaderProgram shader = BBSShaders.getModel();

                RenderSystem.setShader(() -> shader);
                RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
                
                boolean needBlendUI = tint.a < 0.999F || this.hasTranslucentLayer;
                
                if (needBlendUI)
                {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }
                else
                {
                    RenderSystem.disableBlend();
                }
                
                RenderSystem.enableCull();
                ModelVAORenderer.render(shader, this.structureVao, matrices, tint.r, tint.g, tint.b, tint.a, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

                if (this.hasBlockEntityLayer)
                {
                    try
                    {
                        VertexConsumerProvider beConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                        FormRenderingContext beContext = new FormRenderingContext()
                            .set(FormRenderType.PREVIEW, null, matrices, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0F);
                        
                        this.renderBlockEntitiesOnly(beContext, matrices, beConsumers, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                        
                        if (beConsumers instanceof VertexConsumerProvider.Immediate immediate)
                        {
                            immediate.draw();
                        }
                    }
                    catch (Throwable e)
                    {
                    }
                }

                if (this.hasBiomeTintedLayer)
                {
                    try
                    {
                        boolean shadersEnabled = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();
                        VertexConsumerProvider consumersTint = shadersEnabled
                            ? MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
                            : VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

                        FormRenderingContext tintContext = new FormRenderingContext()
                            .set(FormRenderType.PREVIEW, null, matrices, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0F);
                        
                        this.renderBiomeTintedBlocksVanilla(tintContext, matrices, consumersTint, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

                        if (consumersTint instanceof VertexConsumerProvider.Immediate immediate)
                        {
                            immediate.draw();
                        }
                    }
                    catch (Throwable e)
                    {
                    }
                }

                if (this.hasAnimatedLayer)
                {
                    try
                    {
                        boolean shadersEnabled = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();
                        VertexConsumerProvider consumersAnim = shadersEnabled
                            ? MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
                            : VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

                        FormRenderingContext animContext = new FormRenderingContext()
                            .set(FormRenderType.PREVIEW, null, matrices, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0F);
                        
                        this.renderAnimatedBlocksVanilla(animContext, matrices, consumersAnim, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

                        if (consumersAnim instanceof VertexConsumerProvider.Immediate immediate)
                        {
                            immediate.draw();
                        }
                    }
                    catch (Throwable e)
                    {
                    }
                }

                gameRenderer.getLightmapTextureManager().disable();
                gameRenderer.getOverlayTexture().teardownOverlayColor();
                RenderSystem.disableBlend();
                RenderSystem.enableCull();
            }
        }

        matrices.pop();
        /* Restore depth state expected by UI system */
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
    }

    @Override
    protected void render3D(FormRenderingContext context)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            return;
        }

        this.ensureLoaded();
        context.stack.push();

        boolean optimize = BBSSettings.structureOptimization.get();
        boolean picking = context.isPicking();

        if (this.structureVao != null && this.vaoBuiltWithShaders != ModelVAO.isShadersEnabled())
        {
            this.vaoDirty = true;
        }

        /* Detect light setting changes to rebuild VAO */
        StructureLightSettings sl = this.form.structureLight.getRuntimeValue();
        boolean currentEmitLight = (sl != null) ? sl.enabled : this.form.emitLight.get();
        int currentLightIntensity = (sl != null) ? sl.intensity : this.form.lightIntensity.get();

        if (currentEmitLight != this.lastEmitLight || currentLightIntensity != this.lastLightIntensity)
        {
            this.vaoDirty = true;
            this.lastEmitLight = currentEmitLight;
            this.lastLightIntensity = currentLightIntensity;
        }

        if (optimize && (this.structureVao == null || this.vaoDirty))
        {
            this.buildStructureVAO();
        }

        if (!optimize)
        {
            /* If picking, render with VAO (picking) and picking shader to get full silhouette */
            if (picking)
            {
                if (this.structureVaoPicking == null || this.vaoPickingDirty)
                {
                    this.buildStructureVAOPicking();
                }

                Color tint3D = this.form.color.get();
                int light = 0;

                GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
                gameRenderer.getLightmapTextureManager().enable();
                gameRenderer.getOverlayTexture().setupOverlayColor();

                this.setupTarget(context, BBSShaders.getPickerModelsProgram());
                RenderSystem.setShader(BBSShaders::getPickerModelsProgram);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
                ModelVAORenderer.render(BBSShaders.getPickerModelsProgram(), this.structureVaoPicking, context.stack, tint3D.r, tint3D.g, tint3D.b, tint3D.a, light, context.overlay);

                gameRenderer.getLightmapTextureManager().disable();
                gameRenderer.getOverlayTexture().teardownOverlayColor();

                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_LEQUAL);
            }
            else
            {
                /* BufferBuilder mode: use vanilla/culling pipeline with better lighting */
                int light = context.light;
                boolean shaders = this.isShadersActive();
                VertexConsumerProvider consumers = shaders
                    ? MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
                    : VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

                /* Align state handling with VAO path to avoid state leaks */
                /* affecting the first model rendered after. */
                GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
                gameRenderer.getLightmapTextureManager().enable();
                gameRenderer.getOverlayTexture().setupOverlayColor();
                /* Ensure block atlas is active when starting pass */
                RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

                try
                {
                    this.renderStructureCulledWorld(context, context.stack, consumers, light, context.overlay, shaders, null);
                    if (consumers instanceof VertexConsumerProvider.Immediate immediate)
                    {
                        immediate.draw();
                    }
                }
                catch (Throwable e)
                {
                }

                /* Restore state after BufferBuilder pass to avoid contaminating */
                /* next render (models, UI, etc.) */
                gameRenderer.getLightmapTextureManager().disable();
                gameRenderer.getOverlayTexture().teardownOverlayColor();

                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_LEQUAL);
            }
        }
        else if (this.structureVao != null)
        {
            Color tint3D = this.form.color.get();
            int light = context.isPicking() ? 0 : context.light;

            GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
            gameRenderer.getLightmapTextureManager().enable();
            gameRenderer.getOverlayTexture().setupOverlayColor();

            if (context.isPicking())
            {
                if (this.structureVaoPicking == null || this.vaoPickingDirty)
                {
                    this.buildStructureVAOPicking();
                }
                this.setupTarget(context, BBSShaders.getPickerModelsProgram());
                RenderSystem.setShader(BBSShaders::getPickerModelsProgram);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
                ModelVAORenderer.render(BBSShaders.getPickerModelsProgram(), this.structureVaoPicking, context.stack, tint3D.r, tint3D.g, tint3D.b, tint3D.a, light, context.overlay);
            }
            else
            {
                /* Return to own model shader in vanilla */
                ShaderProgram shader = BBSShaders.getModel();

                this.setupTarget(context, shader);
                RenderSystem.setShader(() -> shader);
                RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

                boolean needBlend = tint3D.a < 0.999F || this.hasTranslucentLayer;
                
                if (needBlend)
                {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }
                else
                {
                    RenderSystem.disableBlend();
                }
                
                RenderSystem.enableCull();
                ModelVAORenderer.render(shader, this.structureVao, context.stack, tint3D.r, tint3D.g, tint3D.b, tint3D.a, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, context.overlay);

                if (this.hasBlockEntityLayer)
                {
                    try
                    {
                        VertexConsumerProvider beConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                        
                        this.renderBlockEntitiesOnly(context, context.stack, beConsumers, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, context.overlay);
                        
                        if (beConsumers instanceof VertexConsumerProvider.Immediate immediate)
                        {
                            immediate.draw();
                        }
                    }
                    catch (Throwable e)
                    {
                    }
                }

                if (this.hasBiomeTintedLayer)
                {
                    try
                    {
                        boolean shadersEnabled = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();
                        VertexConsumerProvider consumersTint = shadersEnabled
                            ? MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
                            : VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

                        this.renderBiomeTintedBlocksVanilla(context, context.stack, consumersTint, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, context.overlay);

                        if (consumersTint instanceof VertexConsumerProvider.Immediate immediate)
                        {
                            immediate.draw();
                        }
                    }
                    catch (Throwable e)
                    {
                    }
                }

                if (this.hasAnimatedLayer)
                {
                    try
                    {
                        boolean shadersEnabled = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();
                        VertexConsumerProvider consumersAnim = shadersEnabled
                            ? MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()
                            : VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

                        this.renderAnimatedBlocksVanilla(context, context.stack, consumersAnim, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, context.overlay);

                        if (consumersAnim instanceof VertexConsumerProvider.Immediate immediate)
                        {
                            immediate.draw();
                        }
                    }
                    catch (Throwable e)
                    {
                    }
                }
            }

            gameRenderer.getLightmapTextureManager().disable();
            gameRenderer.getOverlayTexture().teardownOverlayColor();

            /* Restore state if VAO was used */
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }

        CustomVertexConsumerProvider.clearRunnables();
        context.stack.pop();
    }

    private void renderStructure(MatrixStack stack, CustomVertexConsumerProvider consumers, int light, int overlay)
    {
        /* Centering based on real bounds (min/max) to compensate NBT offsets */
        float cx;
        float cy;
        float cz;

        if (this.boundsMin != null && this.boundsMax != null)
        {
            cx = (this.boundsMin.getX() + this.boundsMax.getX()) / 2F;
            cz = (this.boundsMin.getZ() + this.boundsMax.getZ()) / 2F;
            /* Keep on ground: use min Y as base */
            cy = this.boundsMin.getY();
        }
        else
        {
            /* Fallback if no bounds calculated */
            cx = this.size.getX() / 2F;
            cy = 0F;
            cz = this.size.getZ() / 2F;
        }

        /* Determine effective pivot */
        /* Effect: pivot is always calculated automatically */
        float parityXAuto = 0F;
        float parityZAuto = 0F;
        
        if (this.boundsMin != null && this.boundsMax != null)
        {
            int widthX = this.boundsMax.getX() - this.boundsMin.getX() + 1;
            int widthZ = this.boundsMax.getZ() - this.boundsMin.getZ() + 1;
            parityXAuto = (widthX % 2 == 1) ? -0.5F : 0F;
            parityZAuto = (widthZ % 2 == 1) ? -0.5F : 0F;
        }
        
        float pivotX = cx - parityXAuto;
        float pivotY = cy;
        float pivotZ = cz - parityZAuto;

        for (BlockEntry entry : this.blocks)
        {
            stack.push();
            stack.translate(entry.pos.getX() - pivotX, entry.pos.getY() - pivotY, entry.pos.getZ() - pivotZ);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(entry.state, stack, consumers, light, overlay);
            stack.pop();
        }
    }

    /**
     * Render with culling using virtual BlockRenderView to leverage vanilla logic.
     * Maintains same centering and parity as renderStructure.
     */
    private void renderStructureCulledWorld(FormRenderingContext context, MatrixStack stack, VertexConsumerProvider consumers, int light, int overlay, boolean useEntityLayers, LightmapStructureVAOCollector flushTarget)
    {
        /* Centering based on real bounds (min/max) to compensate NBT offsets */
        float cx;
        float cy;
        float cz;

        if (this.boundsMin != null && this.boundsMax != null)
        {
            cx = (this.boundsMin.getX() + this.boundsMax.getX()) / 2F;
            cz = (this.boundsMin.getZ() + this.boundsMax.getZ()) / 2F;
            /* Keep on ground: use min Y as base */
            cy = this.boundsMin.getY();
        }
        else
        {
            /* Fallback if no bounds calculated */
            cx = this.size.getX() / 2F;
            cy = 0F;
            cz = this.size.getZ() / 2F;
        }

        float parityXAuto2 = 0F;
        float parityZAuto2 = 0F;
        
        if (this.boundsMin != null && this.boundsMax != null)
        {
            int widthX = this.boundsMax.getX() - this.boundsMin.getX() + 1;
            int widthZ = this.boundsMax.getZ() - this.boundsMin.getZ() + 1;
            parityXAuto2 = (widthX % 2 == 1) ? -0.5F : 0F;
            parityZAuto2 = (widthZ % 2 == 1) ? -0.5F : 0F;
        }
        
        float pivotX = cx - parityXAuto2;
        float pivotY = cy;
        float pivotZ = cz - parityZAuto2;

        /* Build virtual view with all blocks */
        List<VirtualBlockRenderView.Entry> entries = (this.entriesCache != null)
            ? Arrays.asList(this.entriesCache)
            : Collections.emptyList();
            
        /* Resolve unified structure light settings with legacy fallback */
        boolean lightsEnabled;
        int lightIntensity;
        StructureLightSettings slRuntime = this.form.structureLight.getRuntimeValue();
        
        if (slRuntime != null)
        {
            lightsEnabled = slRuntime.enabled;
            lightIntensity = slRuntime.intensity;
        }
        else
        {
            lightsEnabled = this.form.emitLight.get();
            lightIntensity = this.form.lightIntensity.get();
        }

        StructureVirtualBlockRenderView view = new StructureVirtualBlockRenderView(entries);
        view.setBiomeOverride(this.form.biomeId.get())
            .setLightsEnabled(lightsEnabled)
            .setLightIntensity(lightIntensity);
        
        if (lightsEnabled)
        {
            view.setVirtualMode(true, lightIntensity);
        }

        BlockEntityRenderDispatcher beDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

        /* World anchor position: for items/UI use player position (more stable) */
        /* to avoid anchoring at (0,0,0) and getting low world light. */
        BlockPos anchor;
        boolean isItemContext = (context.type == FormRenderType.ITEM
            || context.type == FormRenderType.ITEM_FP
            || context.type == FormRenderType.ITEM_TP
            || context.type == FormRenderType.ITEM_INVENTORY);
            
        if (isItemContext || context.entity == null)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null)
            {
                anchor = mc.player.getBlockPos();
            }
            else
            {
                anchor = BlockPos.ORIGIN;
            }
        }
        else
        {
            anchor = new BlockPos(
                (int)Math.floor(context.entity.getX()),
                (int)Math.floor(context.entity.getY()),
                (int)Math.floor(context.entity.getZ())
            );
        }

        /* Define base offset from center/parity so BlockRenderView */
        /* can translate light/color queries to real world coordinates. */
        int baseDx = (int)Math.floor(-pivotX);
        int baseDy = (int)Math.floor(-pivotY);
        int baseDz = (int)Math.floor(-pivotZ);
        view.setWorldAnchor(anchor, baseDx, baseDy, baseDz)
            /* In UI/thumbnail/inventory item, force max sky light to avoid darkening */
            /* EXCEPT if capturing VAO, where we want real calculated lighting (VirtualBlockRenderView). */
            .setForceMaxSkyLight(!this.capturingVAO && (context.ui
                || context.type == FormRenderType.PREVIEW
                || context.type == FormRenderType.ITEM_INVENTORY));

        for (BlockEntry entry : this.blocks)
        {
            stack.push();
            stack.translate(entry.pos.getX() - pivotX, entry.pos.getY() - pivotY, entry.pos.getZ() - pivotZ);

            /* During normal VAO capture, skip blocks with animated textures */
            /* or biome tint to avoid double drawing and flickering. */
            /* In picking capture (capturingIncludeSpecialBlocks=true), include them. */
            if (this.capturingVAO && !this.capturingIncludeSpecialBlocks && (this.isAnimatedTexture(entry.state) || this.isBiomeTinted(entry.state)))
            {
                stack.pop();
                continue;
            }

            /* Use entity layer for blocks when rendering with WorldRenderer entity vertex provider. */
            /* This ensures compatibility with shaders (Iris/Sodium) for translucent and special layers. */
            RenderLayer layer = useEntityLayers
                ? RenderLayers.getEntityBlockLayer(entry.state, false)
                : RenderLayers.getBlockLayer(entry.state);

            /* If global opacity (<1), force translucent layer for all structure blocks, */
            /* so alpha applies even to solid/cutout geometry. */
            /* In shaders mode (useEntityLayers=true) use translucent entity variant WITH CULL */
            /* to preserve culling and avoid double faces with packs. */
            float globalAlpha = this.form.color.get().a;
            if (globalAlpha < 0.999F)
            {
                layer = useEntityLayers
                    ? TexturedRenderLayers.getEntityTranslucentCull()
                    : RenderLayer.getTranslucent();
            }

            VertexConsumer vc = consumers.getBuffer(layer);
            /* Wrap consumer with tint/opacity to guarantee coloration */
            /* also when using entity buffers (shader compatibility). */
            Color tint = this.form.color.get();
            Function<VertexConsumer, VertexConsumer> recolor = BBSRendering.getColorConsumer(tint);
            
            if (recolor != null)
            {
                vc = recolor.apply(vc);
            }
            
            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(entry.state, entry.pos, view, stack, vc, true, Random.create());

            if (flushTarget != null && flushTarget.getQuadIndex() != 0)
            {
                flushTarget.flush();
            }

            /* Render blocks with entity (chests, beds, signs, skulls, etc.) */
            Block block = entry.state.getBlock();
            if (!this.capturingVAO && block instanceof BlockEntityProvider)
            {
                /* Align BE position with actual drawing location */
                int dx = (int)Math.floor(entry.pos.getX() - pivotX);
                int dy = (int)Math.floor(entry.pos.getY() - pivotY);
                int dz = (int)Math.floor(entry.pos.getZ() - pivotZ);
                BlockPos worldPos = anchor.add(dx, dy, dz);

                BlockEntity be = ((BlockEntityProvider) block).createBlockEntity(worldPos, entry.state);
                if (be != null)
                {
                    if (entry.nbt != null)
                    {
                        be.readNbt(entry.nbt);
                    }

                    /* Associate real world so renderer can query light and effects */
                    if (MinecraftClient.getInstance().world != null)
                    {
                        be.setWorld(MinecraftClient.getInstance().world);
                    }

                    /* Diagnostic: check if renderer exists for this BE */
                    BlockEntityRenderer<?> renderer = beDispatcher.get(be);

                    /* Render BE directly with renderer to avoid internal translations */
                    /* based on camera/world position that misalign drawing relative to local matrix. */
                    /* BE Light: use virtual view to incorporate artificial light */
                    /* from buffer, combining sky and block as in vanilla pipeline. */
                    int skyLight = view.getLightLevel(LightType.SKY, entry.pos);
                    int blockLight = view.getLightLevel(LightType.BLOCK, entry.pos);
                    /* LightmapTextureManager.pack expects block light first, then sky light. */
                    int beLight = LightmapTextureManager.pack(blockLight, skyLight);

                    if (renderer != null)
                    {
                        @SuppressWarnings({"rawtypes", "unchecked"})
                        BlockEntityRenderer raw = (BlockEntityRenderer) renderer;

                        /* Apply global tint/alpha and force translucent layer on cutout layers */
                        /* so Block Entities also respect opacity. */
                        CustomVertexConsumerProvider beProvider = FormUtilsClient.getProvider();
                        beProvider.setSubstitute(BBSRendering.getColorConsumer(this.form.color.get()));
                        
                        try
                        {
                            raw.render(be, 0F, stack, beProvider, beLight, overlay);
                        }
                        finally
                        {
                            beProvider.draw();
                            beProvider.setSubstitute(null);
                            CustomVertexConsumerProvider.clearRunnables();
                        }
                    }
                }
            }

            stack.pop();
        }

        /* Important: if Sodium/Iris is active, recolor wrapper uses */
        /* global static state (RecolorVertexConsumer.newColor). Ensure */
        /* it is reset after this pass so UI doesn't inherit the tint. */
        RecolorVertexConsumer.newColor = null;
    }

    /**
     * Specialized render: draws only blocks with animated textures (portal, water, lava)
     * using vanilla TranslucentMovingBlock layer to get continuous animation.
     * Reuses same centering/parity calculation and virtual world view.
     */
    private void renderAnimatedBlocksVanilla(FormRenderingContext context, MatrixStack stack, VertexConsumerProvider consumers, int light, int overlay)
    {
        /* Ensure block atlas active */
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        /* Centering based on real bounds (min/max) */
        float cx;
        float cy;
        float cz;

        if (this.boundsMin != null && this.boundsMax != null)
        {
            cx = (this.boundsMin.getX() + this.boundsMax.getX()) / 2F;
            cz = (this.boundsMin.getZ() + this.boundsMax.getZ()) / 2F;
            cy = this.boundsMin.getY();
        }
        else
        {
            cx = this.size.getX() / 2F;
            cy = 0F;
            cz = this.size.getZ() / 2F;
        }

        float parityXAuto3 = 0F;
        float parityZAuto3 = 0F;
        
        if (this.boundsMin != null && this.boundsMax != null)
        {
            int widthX = this.boundsMax.getX() - this.boundsMin.getX() + 1;
            int widthZ = this.boundsMax.getZ() - this.boundsMin.getZ() + 1;
            parityXAuto3 = (widthX % 2 == 1) ? -0.5F : 0F;
            parityZAuto3 = (widthZ % 2 == 1) ? -0.5F : 0F;
        }
        
        float pivotX = cx - parityXAuto3;
        float pivotY = cy;
        float pivotZ = cz - parityZAuto3;

        /* Virtual view for correct culling/colors/light */
        VirtualBlockRenderView view = this.cachedView;
        if (view == null)
        {
            List<VirtualBlockRenderView.Entry> entries = (this.entriesCache != null)
                ? Arrays.asList(this.entriesCache)
                : Collections.emptyList();
            view = new VirtualBlockRenderView(entries);
        }

        /* Resolve unified structure light settings with legacy fallback */
        boolean lightsEnabled2;
        int lightIntensity2;
        StructureLightSettings slRuntime2 = this.form.structureLight.getRuntimeValue();
        
        if (slRuntime2 != null)
        {
            lightsEnabled2 = slRuntime2.enabled;
            lightIntensity2 = slRuntime2.intensity;
        }
        else
        {
            lightsEnabled2 = this.form.emitLight.get();
            lightIntensity2 = this.form.lightIntensity.get();
        }

        view.setBiomeOverride(this.form.biomeId.get())
            .setLightsEnabled(lightsEnabled2)
            .setLightIntensity(lightIntensity2);

        /* World anchor: prefer player position in UI/items */
        BlockPos anchor;
        boolean isItemContextAnim = (context.type == FormRenderType.ITEM
            || context.type == FormRenderType.ITEM_FP
            || context.type == FormRenderType.ITEM_TP
            || context.type == FormRenderType.ITEM_INVENTORY);
            
        if (isItemContextAnim || context.entity == null)
        {
            MinecraftClient mc2 = MinecraftClient.getInstance();
            anchor = (mc2.player != null) ? mc2.player.getBlockPos() : BlockPos.ORIGIN;
        }
        else
        {
            anchor = new BlockPos(
                (int)Math.floor(context.entity.getX()),
                (int)Math.floor(context.entity.getY()),
                (int)Math.floor(context.entity.getZ())
            );
        }

        int baseDx = (int)Math.floor(-pivotX);
        int baseDy = (int)Math.floor(-pivotY);
        int baseDz = (int)Math.floor(-pivotZ);
        view.setWorldAnchor(anchor, baseDx, baseDy, baseDz)
            .setForceMaxSkyLight(context.ui
                || context.type == FormRenderType.PREVIEW
                || context.type == FormRenderType.ITEM_INVENTORY);

        for (BlockEntry entry : this.animatedBlocks)
        {
            stack.push();
            stack.translate(entry.pos.getX() - pivotX, entry.pos.getY() - pivotY, entry.pos.getZ() - pivotZ);

            /* Layer selection: choose based on animated block */
            boolean shadersEnabled = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();
            RenderLayer layer;
            FluidState fsAnim = entry.state.getFluidState();
            
            if (entry.state.isOf(Blocks.NETHER_PORTAL) || (fsAnim != null && (fsAnim.getFluid() == Fluids.WATER || fsAnim.getFluid() == Fluids.FLOWING_WATER || fsAnim.getFluid() == Fluids.LAVA || fsAnim.getFluid() == Fluids.FLOWING_LAVA)))
            {
                layer = shadersEnabled ? RenderLayers.getEntityBlockLayer(entry.state, true) : RenderLayer.getTranslucentMovingBlock();
            }
            else
            {
                layer = shadersEnabled ? RenderLayers.getEntityBlockLayer(entry.state, false) : RenderLayers.getBlockLayer(entry.state);
            }

            /* If global alpha, prefer translucent entity layer in shaders to ensure smooth fade */
            float globalAlphaAnim = this.form.color.get().a;
            if (globalAlphaAnim < 0.999F)
            {
                layer = shadersEnabled
                    ? TexturedRenderLayers.getEntityTranslucentCull()
                    : RenderLayer.getTranslucentMovingBlock();
            }

            /* Apply global alpha as recolor */
            VertexConsumer vc = consumers.getBuffer(layer);
            Color tint = this.form.color.get();
            Function<VertexConsumer, VertexConsumer> recolor = BBSRendering.getColorConsumer(tint);
            
            if (recolor != null)
            {
                vc = recolor.apply(vc);
            }

            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(entry.state, entry.pos, view, stack, vc, true, Random.create());
            stack.pop();
        }

        /* Reset global color state (Sodium/Iris) after animated pass */
        RecolorVertexConsumer.newColor = null;
    }

    /**
     * Renders blocks that require biome tint (leaves, grass, vines, lily pad) using vanilla layers.
     */
    private void renderBiomeTintedBlocksVanilla(FormRenderingContext context, MatrixStack stack, VertexConsumerProvider consumers, int light, int overlay)
    {
        /* Ensure correct blending state for translucent layers */
        RenderSystem.enableBlend();
        
        /* Centering based on real bounds (min/max) */
        float cx;
        float cy;
        float cz;

        if (this.boundsMin != null && this.boundsMax != null)
        {
            cx = (this.boundsMin.getX() + this.boundsMax.getX()) / 2F;
            cz = (this.boundsMin.getZ() + this.boundsMax.getZ()) / 2F;
            cy = this.boundsMin.getY();
        }
        else
        {
            cx = this.size.getX() / 2f;
            cy = 0f;
            cz = this.size.getZ() / 2f;
        }

        float parityXAuto4 = 0f;
        float parityZAuto4 = 0f;
        
        if (this.boundsMin != null && this.boundsMax != null)
        {
            int widthX = this.boundsMax.getX() - this.boundsMin.getX() + 1;
            int widthZ = this.boundsMax.getZ() - this.boundsMin.getZ() + 1;
            parityXAuto4 = (widthX % 2 == 1) ? -0.5f : 0f;
            parityZAuto4 = (widthZ % 2 == 1) ? -0.5f : 0f;
        }
        
        float pivotX = cx - parityXAuto4;
        float pivotY = cy;
        float pivotZ = cz - parityZAuto4;

        /* Virtual view */
        VirtualBlockRenderView view = this.cachedView;
        if (view == null)
        {
            List<VirtualBlockRenderView.Entry> entries = (this.entriesCache != null)
                ? Arrays.asList(this.entriesCache)
                : Collections.emptyList();
            view = new VirtualBlockRenderView(entries);
        }

        /* Resolve unified structure light settings with legacy fallback */
        boolean lightsEnabled3;
        int lightIntensity3;
        StructureLightSettings slRuntime3 = this.form.structureLight.getRuntimeValue();
        
        if (slRuntime3 != null)
        {
            lightsEnabled3 = slRuntime3.enabled;
            lightIntensity3 = slRuntime3.intensity;
        }
        else
        {
            lightsEnabled3 = this.form.emitLight.get();
            lightIntensity3 = this.form.lightIntensity.get();
        }

        view.setBiomeOverride(this.form.biomeId.get())
            .setLightsEnabled(lightsEnabled3)
            .setLightIntensity(lightIntensity3);

        /* World anchor */
        BlockPos anchor;
        boolean isItemContextTint = (context.type == FormRenderType.ITEM
            || context.type == FormRenderType.ITEM_FP
            || context.type == FormRenderType.ITEM_TP
            || context.type == FormRenderType.ITEM_INVENTORY);
            
        if (isItemContextTint || context.entity == null)
        {
            MinecraftClient mc3 = MinecraftClient.getInstance();
            anchor = (mc3.player != null) ? mc3.player.getBlockPos() : BlockPos.ORIGIN;
        }
        else
        {
            anchor = new BlockPos(
                (int)Math.floor(context.entity.getX()),
                (int)Math.floor(context.entity.getY()),
                (int)Math.floor(context.entity.getZ())
            );
        }

        int baseDx = (int)Math.floor(-pivotX);
        int baseDy = (int)Math.floor(-pivotY);
        int baseDz = (int)Math.floor(-pivotZ);
        view.setWorldAnchor(anchor, baseDx, baseDy, baseDz)
            .setForceMaxSkyLight(context.ui
                || context.type == FormRenderType.PREVIEW
                || context.type == FormRenderType.ITEM_INVENTORY);

        for (BlockEntry entry : this.biomeTintedBlocks)
        {
            stack.push();
            stack.translate(entry.pos.getX() - pivotX, entry.pos.getY() - pivotY, entry.pos.getZ() - pivotZ);

            /* Layer according to state; in shaders use entity variant for packs */
            boolean shadersEnabledTint = BBSRendering.isIrisShadersEnabled() && BBSRendering.isRenderingWorld();
            RenderLayer layer = shadersEnabledTint
                ? RenderLayers.getEntityBlockLayer(entry.state, false)
                : RenderLayers.getBlockLayer(entry.state);

            /* If global opacity (<1), force translucent layer so alpha */
            /* applies to materials originally cutout/cull and they don't "disappear". */
            float globalAlpha = this.form.color.get().a;
            if (globalAlpha < 0.999F)
            {
                layer = shadersEnabledTint ? TexturedRenderLayers.getEntityTranslucentCull() : RenderLayer.getTranslucent();
            }

            VertexConsumer vc = consumers.getBuffer(layer);
            Color tint = this.form.color.get();
            Function<VertexConsumer, VertexConsumer> recolor = BBSRendering.getColorConsumer(tint);
            
            if (recolor != null)
            {
                vc = recolor.apply(vc);
            }

            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(entry.state, entry.pos, view, stack, vc, true, Random.create());
            stack.pop();
        }

        /* Restore state */
        RenderSystem.disableBlend();
        /* Reset global color state (Sodium/Iris) to avoid UI tinting */
        RecolorVertexConsumer.newColor = null;
    }

    /** Determine if the block requires texture animation (portal/water/lava). */
    private boolean isAnimatedTexture(BlockState state)
    {
        if (state == null)
        {
            return false;
        }

        /* Nether Portal */
        if (state.isOf(Blocks.NETHER_PORTAL))
        {
            return true;
        }

        /* Fluids: water and lava (including flowing variants) */
        FluidState fs = state.getFluidState();
        if (fs != null)
        {
            if (fs.getFluid() == Fluids.WATER || fs.getFluid() == Fluids.FLOWING_WATER ||
                fs.getFluid() == Fluids.LAVA || fs.getFluid() == Fluids.FLOWING_LAVA)
            {
                return true;
            }
        }

        /* Normal fire and soul fire: texture animation in atlas */
        if (state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE))
        {
            return true;
        }

        return false;
    }

    /** Heuristic: determines if the block requires special tint (biome/redstone). */
    private boolean isBiomeTinted(BlockState state)
    {
        if (state == null) return false;
        Block b = state.getBlock();
        return (b instanceof LeavesBlock)
            || (b instanceof GrassBlock)
            || (b instanceof VineBlock)
            || (b instanceof LilyPadBlock)
            /* Redstone wire uses power-dependent color provider */
            || (b instanceof RedstoneWireBlock)
            /* Additional vegetation requiring biome tint */
            || state.isOf(Blocks.FERN)
            || state.isOf(Blocks.SUGAR_CANE)
            || (b instanceof StemBlock)
            || (b instanceof AttachedStemBlock)
            || state.isOf(Blocks.SHORT_GRASS)
            || state.isOf(Blocks.TALL_GRASS)
            || state.isOf(Blocks.LARGE_FERN);
    }

    /**
     * Renders only Block Entities (chests, beds, signs, skulls, etc.) over the structure already drawn via VAO.
     * Reuses same centering/parity and world anchor as culled render.
     */
    private void renderBlockEntitiesOnly(FormRenderingContext context, MatrixStack stack, VertexConsumerProvider consumers, int light, int overlay)
    {
        /* Calculate effective pivot same as in renderStructureCulledWorld */
        float cx;
        float cy;
        float cz;

        if (this.boundsMin != null && this.boundsMax != null)
        {
            cx = (this.boundsMin.getX() + this.boundsMax.getX()) / 2F;
            cz = (this.boundsMin.getZ() + this.boundsMax.getZ()) / 2F;
            cy = this.boundsMin.getY();
        }
        else
        {
            cx = this.size.getX() / 2F;
            cy = 0F;
            cz = this.size.getZ() / 2F;
        }

        float pivotX;
        float pivotY;
        float pivotZ;
        PivotSettings pivotSettingsRuntime = this.form.pivot.getRuntimeValue();
        boolean useAuto = pivotSettingsRuntime != null ? pivotSettingsRuntime.auto : this.form.autoPivot.get();
        
        if (useAuto)
        {
            float parityXAuto = 0F;
            float parityZAuto = 0F;
            
            if (this.boundsMin != null && this.boundsMax != null)
            {
                int widthX = this.boundsMax.getX() - this.boundsMin.getX() + 1;
                int widthZ = this.boundsMax.getZ() - this.boundsMin.getZ() + 1;
                parityXAuto = (widthX % 2 == 1) ? -0.5F : 0F;
                parityZAuto = (widthZ % 2 == 1) ? -0.5F : 0F;
            }
            pivotX = cx - parityXAuto;
            pivotY = cy;
            pivotZ = cz - parityZAuto;
        }
        else
        {
            if (pivotSettingsRuntime != null)
            {
                pivotX = pivotSettingsRuntime.pivot.x;
                pivotY = pivotSettingsRuntime.pivot.y;
                pivotZ = pivotSettingsRuntime.pivot.z;
            }
            else
            {
                pivotX = this.form.pivotX.get();
                pivotY = this.form.pivotY.get();
                pivotZ = this.form.pivotZ.get();
            }
        }

        /* World anchor */
        BlockPos anchor;
        if (context.entity != null)
        {
            anchor = new BlockPos(
                (int)Math.floor(context.entity.getX()),
                (int)Math.floor(context.entity.getY()),
                (int)Math.floor(context.entity.getZ())
            );
        }
        else
        {
            anchor = BlockPos.ORIGIN;
        }

        BlockEntityRenderDispatcher beDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

        for (BlockEntry entry : this.blockEntitiesList)
        {
            Block block = entry.state.getBlock();
            /* We already filtered in parseStructure, but keep getting 'block' */
            /* for instanceof (which should be true) and cast. */
            if (!(block instanceof BlockEntityProvider))
            {
                continue;
            }

            stack.push();
            stack.translate(entry.pos.getX() - pivotX, entry.pos.getY() - pivotY, entry.pos.getZ() - pivotZ);

            int dx = (int)Math.floor(entry.pos.getX() - pivotX);
            int dy = (int)Math.floor(entry.pos.getY() - pivotY);
            int dz = (int)Math.floor(entry.pos.getZ() - pivotZ);
            BlockPos worldPos = anchor.add(dx, dy, dz);

            BlockEntity be = ((BlockEntityProvider) block).createBlockEntity(worldPos, entry.state);
            if (be != null)
            {
                if (MinecraftClient.getInstance().world != null)
                {
                    be.setWorld(MinecraftClient.getInstance().world);
                }

                BlockEntityRenderer<?> renderer = beDispatcher.get(be);
                /* When rendering BE over VAO, calculate light with a virtual view */
                /* to apply the same artificial lighting. */
                
                /* Use cachedView if available */
                VirtualBlockRenderView beView = this.cachedView;
                if (beView == null)
                {
                    List<VirtualBlockRenderView.Entry> entries = (this.entriesCache != null)
                        ? Arrays.asList(this.entriesCache)
                        : Collections.emptyList();
                    beView = new VirtualBlockRenderView(entries);
                }

                boolean lightsEnabledBE;
                int lightIntensityBE;
                StructureLightSettings slRuntimeBE = this.form.structureLight.getRuntimeValue();
                
                if (slRuntimeBE != null)
                {
                    lightsEnabledBE = slRuntimeBE.enabled;
                    lightIntensityBE = slRuntimeBE.intensity;
                }
                else
                {
                    lightsEnabledBE = this.form.emitLight.get();
                    lightIntensityBE = this.form.lightIntensity.get();
                }
                
                beView.setBiomeOverride(this.form.biomeId.get())
                    .setLightsEnabled(lightsEnabledBE)
                    .setLightIntensity(lightIntensityBE)
                    .setWorldAnchor(anchor, (int)Math.floor(-pivotX), (int)Math.floor(-pivotY), (int)Math.floor(-pivotZ));

                int skyLight = beView.getLightLevel(LightType.SKY, entry.pos);
                int blockLight = beView.getLightLevel(LightType.BLOCK, entry.pos);
                /* LightmapTextureManager.pack expects block light first, then sky light. */
                int beLight = LightmapTextureManager.pack(blockLight, skyLight);

                if (renderer != null)
                {
                    @SuppressWarnings({"rawtypes", "unchecked"})
                    BlockEntityRenderer raw = (BlockEntityRenderer) renderer;

                    /* Apply global tint always to Block Entities, isolating the provider */
                    CustomVertexConsumerProvider beProvider = FormUtilsClient.getProvider();
                    beProvider.setSubstitute(BBSRendering.getColorConsumer(this.form.color.get()));
                    try
                    {
                        raw.render(be, 0F, stack, beProvider, beLight, overlay);
                    }
                    finally
                    {
                        beProvider.draw();
                        beProvider.setSubstitute(null);
                        CustomVertexConsumerProvider.clearRunnables();
                    }
                }
            }

            stack.pop();
        }
    }

    /**
     * Detects if shaders are active (Iris). Avoids hard dependencies using reflection.
     */
    private boolean isShadersActive()
    {
        try
        {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            Object result = apiClass.getMethod("isShaderPackInUse").invoke(api);
            return result instanceof Boolean && (Boolean) result;
        }
        catch (Throwable e)
        {
        }

        return false;
    }

    private void ensureLoaded()
    {
        String file = this.form.structureFile.get();

        if (file == null || file.isEmpty())
        {
            /* Nothing selected; clear to avoid ghost render */
            this.blocks.clear();
            this.animatedBlocks.clear();
            this.biomeTintedBlocks.clear();
            this.blockEntitiesList.clear();
            this.entriesCache = null;
            this.cachedView = null;
            this.size = BlockPos.ORIGIN;
            this.boundsMin = null;
            this.boundsMax = null;
            this.lastFile = null;
            this.vaoDirty = true;
            this.vaoPickingDirty = true;
            
            if (this.structureVao instanceof ModelVAO)
            {
                ((ModelVAO) this.structureVao).delete();
            }
            else if (this.structureVao instanceof LightmapModelVAO)
            {
                ((LightmapModelVAO) this.structureVao).delete();
            }
            
            this.structureVao = null;
            
            if (this.structureVaoPicking instanceof ModelVAO)
            {
                ((ModelVAO) this.structureVaoPicking).delete();
            }
            
            this.structureVaoPicking = null;
            return;
        }

        if (file.equals(this.lastFile) && !this.blocks.isEmpty())
        {
            return;
        }

        File nbtFile = BBSMod.getProvider().getFile(Link.create(file));

        this.blocks.clear();
        this.animatedBlocks.clear();
        this.biomeTintedBlocks.clear();
        this.blockEntitiesList.clear();
        this.entriesCache = null;
        this.cachedView = null;
        this.size = BlockPos.ORIGIN;
        this.boundsMin = null;
        this.boundsMax = null;
        this.lastFile = file;
        this.vaoDirty = true;
        this.vaoPickingDirty = true;
        
        if (this.structureVao instanceof ModelVAO)
        {
            ((ModelVAO) this.structureVao).delete();
        }
        else if (this.structureVao instanceof LightmapModelVAO)
        {
            ((LightmapModelVAO) this.structureVao).delete();
        }
        
        this.structureVao = null;
        
        if (this.structureVaoPicking instanceof ModelVAO)
        {
            ((ModelVAO) this.structureVaoPicking).delete();
        }
        
        this.structureVaoPicking = null;

        /* Try to read as external file if exists; otherwise use InputStream from internal assets */
        if (nbtFile != null && nbtFile.exists())
        {
            try
            {
                NbtCompound root = NbtIo.readCompressed(nbtFile.toPath(), NbtTagSizeTracker.ofUnlimitedBytes());
                this.parseStructure(root);
                return;
            }
            catch (IOException e)
            {
            }
        }

        /* If no File (internal assets), read via InputStream from provider */
        try (InputStream is = BBSMod.getProvider().getAsset(Link.create(file)))
        {
            try
            {
                NbtCompound root = NbtIo.readCompressed(is, NbtTagSizeTracker.ofUnlimitedBytes());
                this.parseStructure(root);
            }
            catch (IOException e)
            {
            }
        }
        catch (Exception e)
        {
        }
    }

    private void buildStructureVAO()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            return;
        }

        /* Capture geometry in a VAO using vanilla pipeline but substituting the consumer */
        CustomVertexConsumerProvider provider = FormUtilsClient.getProvider();
        StructureVAOCollector collector = new StructureVAOCollector();
        boolean shaders = ModelVAO.isShadersEnabled();
        
        try
        {
            collector.setComputeTangents(shaders);
        }
        catch (Throwable e)
        {
        }

        this.vaoBuiltWithShaders = shaders;

        /* NEW: Wrapper for lightmap support */
        /* Always use light wrapper to ensure lightmap data is captured for both Vanilla and Shaders */
        LightmapStructureVAOCollector lightWrapper = new LightmapStructureVAOCollector(collector);
        /* Virtual light is automatically handled in renderStructureCulledWorld via VirtualBlockRenderView */

        /* Substitute any consumer with our collector */
        final VertexConsumer finalCollector = lightWrapper;
        provider.setSubstitute(vc -> finalCollector);

        MatrixStack captureStack = new MatrixStack();
        FormRenderingContext captureContext = new FormRenderingContext()
            .set(FormRenderType.PREVIEW, null, captureStack, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0F);

        try
        {
            GraphicsMode gm = MinecraftClient.getInstance().options.getGraphicsMode().getValue();
            RenderLayers.setFancyGraphicsOrBetter(gm != GraphicsMode.FAST);
        }
        catch (Throwable e)
        {
        }

        boolean useEntityLayers = false; /* capture with block layers */
        /* Avoid rendering BlockEntities during capture to not mix atlases. */
        this.capturingVAO = true;
        this.capturingIncludeSpecialBlocks = false; /* for normal VAO, skip animated/biome */
        
        try
        {
            this.renderStructureCulledWorld(captureContext, captureStack, provider, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, useEntityLayers, lightWrapper);
        }
        finally
        {
            this.capturingVAO = false;
            this.capturingIncludeSpecialBlocks = false;
        }

        provider.draw();
        provider.setSubstitute(null);

        if (this.structureVao instanceof ModelVAO)
        {
            ((ModelVAO) this.structureVao).delete();
        }
        else if (this.structureVao instanceof LightmapModelVAO)
        {
            ((LightmapModelVAO) this.structureVao).delete();
        }

        ModelVAOData data = collector.toData();
        int[] lightData = lightWrapper.getLightmapData();
        
        this.structureVao = new LightmapModelVAO(data, lightData);
        this.vaoDirty = false;
    }

    /**
     * Builds the picking VAO.
     * so that the selection silhouette covers the entire structure.
     */
    private void buildStructureVAOPicking()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            return; 
        }
        
        CustomVertexConsumerProvider provider = FormUtilsClient.getProvider();
        StructureVAOCollector collector = new StructureVAOCollector();
        
        try
        {
            collector.setComputeTangents(ModelVAO.isShadersEnabled());
        }
        catch (Throwable e)
        {
        }
        
        provider.setSubstitute(vc -> collector);

        MatrixStack captureStack = new MatrixStack();
        FormRenderingContext captureContext = new FormRenderingContext()
            .set(FormRenderType.PREVIEW, null, captureStack, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0F);

        try
        {
            GraphicsMode gm = MinecraftClient.getInstance().options.getGraphicsMode().getValue();
            RenderLayers.setFancyGraphicsOrBetter(gm != GraphicsMode.FAST);
        }
        catch (Throwable e)
        {
        }

        boolean useEntityLayers = false;
        this.capturingVAO = true;
        this.capturingIncludeSpecialBlocks = true; /* include animated and biome for picking */
        
        try
        {
            this.renderStructureCulledWorld(captureContext, captureStack, provider, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, useEntityLayers, null);
        }
        finally
        {
            this.capturingVAO = false;
            this.capturingIncludeSpecialBlocks = false;
        }

        provider.draw();
        provider.setSubstitute(null);

        if (this.structureVaoPicking instanceof ModelVAO)
        {
            ((ModelVAO) this.structureVaoPicking).delete();
        }

        ModelVAOData data = collector.toData();
        this.structureVaoPicking = new ModelVAO(data);
        this.vaoPickingDirty = false;
    }

    private void parseStructure(NbtCompound root)
    {
        /* Size */
        if (root.contains("size", NbtElement.INT_ARRAY_TYPE))
        {
            int[] sz = root.getIntArray("size");
            if (sz.length >= 3)
            {
                this.size = new BlockPos(sz[0], sz[1], sz[2]);
            }
        }

        /* Palette -> state list */
        List<BlockState> paletteStates = new ArrayList<>();
        if (root.contains("palette", NbtElement.LIST_TYPE))
        {
            NbtList palette = root.getList("palette", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < palette.size(); i++)
            {
                NbtCompound entry = palette.getCompound(i);
                BlockState state = this.readBlockState(entry);
                paletteStates.add(state);
            }
        }

        /* Blocks */
        if (root.contains("blocks", NbtElement.LIST_TYPE))
        {
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

            NbtList list = root.getList("blocks", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++)
            {
                NbtCompound be = list.getCompound(i);

                BlockPos pos = this.readBlockPos(be.getList("pos", NbtElement.INT_TYPE));
                int stateIndex = be.getInt("state");
                NbtCompound nbt = be.contains("nbt") ? be.getCompound("nbt") : null;

                if (stateIndex >= 0 && stateIndex < paletteStates.size())
                {
                    BlockState state = paletteStates.get(stateIndex);
                    if (state == null || state.isAir())
                    {
                        continue;
                    }
                    this.blocks.add(new BlockEntry(state, pos, nbt));

                    /* Update bounds */
                    if (pos.getX() < minX) minX = pos.getX();
                    if (pos.getY() < minY) minY = pos.getY();
                    if (pos.getZ() < minZ) minZ = pos.getZ();
                    if (pos.getX() > maxX) maxX = pos.getX();
                    if (pos.getY() > maxY) maxY = pos.getY();
                    if (pos.getZ() > maxZ) maxZ = pos.getZ();
                }
            }

            if (!this.blocks.isEmpty())
            {
                this.boundsMin = new BlockPos(minX, minY, minZ);
                this.boundsMax = new BlockPos(maxX, maxY, maxZ);

                boolean translucent = false;
                boolean cutout = false;
                boolean animated = false;
                boolean biomeTinted = false;
                boolean blockEntities = false;
                VirtualBlockRenderView.Entry[] cache = new VirtualBlockRenderView.Entry[this.blocks.size()];
                
                for (int i = 0; i < this.blocks.size(); i++)
                {
                    BlockEntry e = this.blocks.get(i);
                    RenderLayer l = RenderLayers.getBlockLayer(e.state);
                    if (l == RenderLayer.getTranslucent() || l == RenderLayer.getTranslucentMovingBlock())
                    {
                        translucent = true;
                    }
                    if (l == RenderLayer.getCutout() || l == RenderLayer.getCutoutMipped())
                    {
                        cutout = true;
                    }
                    if (this.isAnimatedTexture(e.state))
                    {
                        animated = true;
                        this.animatedBlocks.add(e);
                    }
                    if (this.isBiomeTinted(e.state))
                    {
                        biomeTinted = true;
                        this.biomeTintedBlocks.add(e);
                    }
                    if (e.state.getBlock() instanceof BlockEntityProvider)
                    {
                        blockEntities = true;
                        this.blockEntitiesList.add(e);
                    }
                    cache[i] = new VirtualBlockRenderView.Entry(e.state, e.pos);
                }
                
                this.hasTranslucentLayer = translucent;
                this.hasCutoutLayer = cutout;
                this.hasAnimatedLayer = animated;
                this.hasBiomeTintedLayer = biomeTinted;
                this.hasBlockEntityLayer = blockEntities;
                this.entriesCache = cache;
                this.cachedView = new VirtualBlockRenderView(Arrays.asList(cache));
            }
        }
    }

    private BlockPos readBlockPos(NbtList list)
    {
        if (list == null || list.size() < 3)
        {
            return BlockPos.ORIGIN;
        }

        int x = list.getInt(0);
        int y = list.getInt(1);
        int z = list.getInt(2);

        return new BlockPos(x, y, z);
    }

    private BlockState readBlockState(NbtCompound entry)
    {
        String name = entry.getString("Name");
        Block block;
        BlockState state;

        try
        {
            Identifier id = new Identifier(name);
            block = Registries.BLOCK.get(id);
            if (block == null)
            {
                block = Blocks.AIR;
            }
        }
        catch (Exception e)
        {
            block = Blocks.AIR;
        }

        if ("minecraft:jigsaw".equals(name) || block == Blocks.JIGSAW)
        {
            return Blocks.AIR.getDefaultState();
        }

        state = block.getDefaultState();

        if (entry.contains("Properties", NbtElement.COMPOUND_TYPE))
        {
            NbtCompound props = entry.getCompound("Properties");
            for (String key : props.getKeys())
            {
                String value = props.getString(key);
                Property<?> property = block.getStateManager().getProperty(key);

                if (property != null)
                {
                    Optional<?> parsed = property.parse(value);
                    if (parsed.isPresent())
                    {
                        try
                        {
                            @SuppressWarnings({"rawtypes", "unchecked"})
                            Property raw = property;
                            @SuppressWarnings("unchecked")
                            Comparable c = (Comparable) parsed.get();
                            state = state.with(raw, c);
                        }
                        catch (Exception ignored)
                        {}
                    }
                }
            }
        }

        return state;
    }

    private static class BlockEntry
    {
        final BlockState state;
        final BlockPos pos;
        final NbtCompound nbt;

        BlockEntry(BlockState state, BlockPos pos, NbtCompound nbt)
        {
            this.state = state;
            this.pos = pos;
            this.nbt = nbt;
        }
    }

    private static class StructureVirtualBlockRenderView extends VirtualBlockRenderView
    {
        private final List<BlockPos> emitters = new ArrayList<>();
        private final List<Integer> emitterLevels = new ArrayList<>();
        private boolean virtualMode = false;
        private int virtualAmbient = 0;
        private boolean myForceMaxSkyLight = false;

        public StructureVirtualBlockRenderView(List<Entry> entries)
        {
            super(entries);
            this.findEmitters(entries);
        }

        private void findEmitters(List<Entry> entries)
        {
            this.emitters.clear();
            this.emitterLevels.clear();

            for (Entry e : entries)
            {
                BlockState st = e.state;
                int lum = st == null ? 0 : st.getLuminance();
                if (lum > 0)
                {
                    this.emitters.add(e.pos);
                    this.emitterLevels.add(lum);
                }
            }
        }

        public StructureVirtualBlockRenderView setVirtualMode(boolean enabled, int ambient)
        {
            this.virtualMode = enabled;
            this.virtualAmbient = ambient;
            return this;
        }

        @Override
        public VirtualBlockRenderView setForceMaxSkyLight(boolean force)
        {
            this.myForceMaxSkyLight = force;
            return super.setForceMaxSkyLight(force);
        }

        @Override
        public int getLightLevel(LightType type, BlockPos pos)
        {
            if (this.myForceMaxSkyLight)
            {
                return 15;
            }

            /* Always get base world level (or super implementation) */
            int baseLevel = super.getLightLevel(type, pos);

            if (this.virtualMode && type == LightType.BLOCK)
            {
                int max = 0;
                for (int i = 0; i < this.emitters.size(); i++)
                {
                    BlockPos sp = this.emitters.get(i);
                    int L = this.emitterLevels.get(i);
                    int dist = Math.abs(sp.getX() - pos.getX()) + Math.abs(sp.getY() - pos.getY()) + Math.abs(sp.getZ() - pos.getZ());
                    int contrib = L - dist;
                    
                    if (contrib > max)
                    {
                        max = contrib;
                    }
                }
                
                /* Scale virtual light with configured intensity */
                /* Intensity (virtualAmbient) acts as a ceiling for internally emitted light */
                int virtualResult = Math.min(max, this.virtualAmbient);
                
                /* Combine with world light (baseLevel) */
                return Math.max(baseLevel, virtualResult);
            }

            return baseLevel;
        }
    }

    private static class LightmapStructureVAOCollector implements VertexConsumer
    {
        private final StructureVAOCollector delegate;
        private int[] lightData = new int[8192];
        private int lightSize = 0;
        private int[] quadLights = new int[4];
        private int quadIndex = 0;

        /* Virtual light config */
        private int minBlockLight = 0;
        private int minSkyLight = 0;

        public LightmapStructureVAOCollector(StructureVAOCollector delegate)
        {
            this.delegate = delegate;
        }

        public void setVirtualLight(int blockLight, int skyLight)
        {
            this.minBlockLight = blockLight;
            this.minSkyLight = skyLight;
        }

        public StructureVAOCollector getDelegate()
        {
            return this.delegate;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z)
        {
            this.delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha)
        {
            this.delegate.color(red, green, blue, alpha);
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v)
        {
            this.delegate.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v)
        {
            this.delegate.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v)
        {
            /* Apply virtual light: ensure configured minimum light */
            /* Note: u = block light, v = sky light (unpacked values 0-240) */
            int finalBlock = Math.max(u & 0xFFFF, this.minBlockLight);
            int finalSky = Math.max(v & 0xFFFF, this.minSkyLight);
            
            this.quadLights[this.quadIndex] = (finalBlock & 0xFFFF) | ((finalSky & 0xFFFF) << 16);
            this.delegate.light(finalBlock, finalSky);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z)
        {
            this.delegate.normal(x, y, z);
            return this;
        }

        @Override
        public void next()
        {
            this.delegate.next();
            this.quadIndex++;

            if (this.quadIndex == 4)
            {
                this.addLight(this.quadLights[0]);
                this.addLight(this.quadLights[1]);
                this.addLight(this.quadLights[2]);

                this.addLight(this.quadLights[0]);
                this.addLight(this.quadLights[2]);
                this.addLight(this.quadLights[3]);

                this.quadIndex = 0;
            }
        }
        
        @Override
        public void fixedColor(int red, int green, int blue, int alpha)
        {
            /* Delegate default method if possible */
        }

        @Override
        public void unfixColor()
        {
        }

        private void addLight(int l)
        {
            if (this.lightSize >= this.lightData.length)
            {
                int[] n = new int[this.lightData.length * 2];
                System.arraycopy(this.lightData, 0, n, 0, this.lightSize);
                this.lightData = n;
            }
            this.lightData[this.lightSize++] = l;
        }

        public int[] getLightmapData()
        {
            return Arrays.copyOf(this.lightData, this.lightSize);
        }

        public int getQuadIndex()
        {
            return this.quadIndex;
        }

        public void flush()
        {
            if (this.quadIndex != 0)
            {
                while (this.quadIndex != 0)
                {
                    this.vertex(0, 0, 0);
                    this.light(0, 0);
                    this.next();
                }
            }
        }
    }

    private static class LightmapModelVAO implements IModelVAO
    {
        private int vao;
        private int count;
        private int[] buffers;
        private int lightmapBuffer;
        
        public LightmapModelVAO(ModelVAOData data, int[] lightmap)
        {
            if (!RenderSystem.isOnRenderThread())
            {
                this.vao = 0;
                this.count = 0;
                this.buffers = new int[0];
                return;
            }

            this.vao = GL30.glGenVertexArrays();
            
            /* Critical check: if VAO generation failed (returned 0), we must not proceed */
            /* because subsequent glVertexAttribPointer calls would trigger GL_INVALID_OPERATION (Array object is not active) */
            if (this.vao <= 0)
            {
                 this.buffers = new int[0];
                 this.count = 0;
                 return;
            }

            GL30.glBindVertexArray(this.vao);
            
            /* Double check binding */
            if (GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING) != this.vao)
            {
                 this.buffers = new int[0];
                 this.count = 0;
                 return;
            }

            int vertexBuffer = GL15.glGenBuffers();
            int normalBuffer = GL15.glGenBuffers();
            int tangentsBuffer = GL15.glGenBuffers();
            int texCoordBuffer = GL15.glGenBuffers();
            int midTexCoordBuffer = GL15.glGenBuffers();
            this.lightmapBuffer = GL15.glGenBuffers();
            
            this.buffers = new int[] {vertexBuffer, normalBuffer, tangentsBuffer, texCoordBuffer, midTexCoordBuffer, this.lightmapBuffer};

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.vertices(), GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(Attributes.POSITION, 3, GL11.GL_FLOAT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.normals(), GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(Attributes.NORMAL, 3, GL11.GL_FLOAT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.texCoords(), GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(Attributes.TEXTURE_UV, 2, GL11.GL_FLOAT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tangentsBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.tangents(), GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(Attributes.TANGENTS, 4, GL11.GL_FLOAT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, midTexCoordBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.texCoords(), GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(Attributes.MID_TEXTURE_UV, 2, GL11.GL_FLOAT, false, 0, 0);
            
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.lightmapBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lightmap, GL15.GL_DYNAMIC_DRAW);
            /* Changed from IPointer to Pointer to avoid GL_INVALID_OPERATION if shader expects float/normalized inputs */
            /* Using GL_UNSIGNED_SHORT with normalized=false sends raw values (0-240) as floats */
            GL20.glVertexAttribPointer(Attributes.LIGHTMAP_UV, 2, GL11.GL_UNSIGNED_SHORT, false, 0, 0);

            GL20.glEnableVertexAttribArray(Attributes.POSITION);
            GL20.glEnableVertexAttribArray(Attributes.NORMAL);
            GL20.glEnableVertexAttribArray(Attributes.TEXTURE_UV);
            GL20.glEnableVertexAttribArray(Attributes.TANGENTS);
            GL20.glEnableVertexAttribArray(Attributes.MID_TEXTURE_UV);
            GL20.glEnableVertexAttribArray(Attributes.LIGHTMAP_UV);

            GL20.glDisableVertexAttribArray(Attributes.COLOR);
            GL20.glDisableVertexAttribArray(Attributes.OVERLAY_UV);

            this.count = data.vertices().length / 3;
            
            GL30.glBindVertexArray(0);
        }

        public void updateLightmap(int[] lightmap)
        {
            if (!RenderSystem.isOnRenderThread())
            {
                return;
            }

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.lightmapBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lightmap, GL15.GL_DYNAMIC_DRAW);
        }

        public void bindForRender()
        {
            if (!RenderSystem.isOnRenderThread())
            {
                return;
            }

            GL30.glBindVertexArray(this.vao);
        }

        public int getVertexCount()
        {
            return this.count;
        }

        public void delete()
        {
            if (!RenderSystem.isOnRenderThread())
            {
                return;
            }

            GL30.glDeleteVertexArrays(this.vao);

            for (int buffer : this.buffers)
            {
                GL15.glDeleteBuffers(buffer);
            }
        }

        @Override
        public void render(VertexFormat format, float r, float g, float b, float a, int light, int overlay)
        {
            if (!RenderSystem.isOnRenderThread())
            {
                return;
            }

            GL30.glBindVertexArray(this.vao);

            /* Explicitly disable these attributes to ensure constant values are used */
            GL20.glVertexAttrib4f(Attributes.COLOR, r, g, b, a);
            GL20.glVertexAttrib2f(Attributes.OVERLAY_UV, (overlay & 0xffff) / 10.0F, (overlay >> 16 & 0xffff) / 10.0F);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.count);

            GL30.glBindVertexArray(0);
        }
    }
}
