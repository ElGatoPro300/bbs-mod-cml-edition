package mchorse.bbs_mod.forms.renderers;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.client.BBSShaders;
import mchorse.bbs_mod.forms.ITickable;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.ParticleForm;
import mchorse.bbs_mod.particles.ParticleScheme;
import mchorse.bbs_mod.particles.emitter.ParticleEmitter;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.utils.MatrixStackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ParticleFormRenderer extends FormRenderer<ParticleForm> implements ITickable
{
    public static long lastUpdate = 0L;

    private ParticleEmitter emitter;
    private boolean checked;
    private boolean restart;
    private long lastParticleUpdate = lastUpdate;

    public ParticleFormRenderer(ParticleForm form)
    {
        super(form);
    }

    public ParticleEmitter getEmitter()
    {
        return this.emitter;
    }

    public void ensureEmitter(World world, float transition)
    {
        if (this.lastParticleUpdate < lastUpdate)
        {
            this.lastParticleUpdate = lastUpdate;
            this.checked = false;
        }

        if (!this.checked)
        {
            ParticleScheme scheme = BBSModClient.getParticles().load(this.form.effect.get());

            if (scheme != null)
            {
                this.emitter = new ParticleEmitter();
                this.emitter.setScheme(scheme);
                this.emitter.setWorld(world);
            }

            this.checked = true;
        }

        if (this.emitter != null && !BBSRendering.isIrisShadowPass())
        {
            boolean lastPaused = this.emitter.paused;

            this.emitter.paused = this.form.paused.get();

            if (lastPaused != this.emitter.paused && !this.emitter.paused && this.emitter.age > 0 && !this.restart)
            {
                this.restart = true;
            }
        }
    }

    @Override
    public void renderInUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        this.ensureEmitter(MinecraftClient.getInstance().world, context.getTransition());

        ParticleEmitter emitter = this.emitter;

        if (emitter != null && emitter.scheme != null)
        {
            MatrixStack stack = context.batcher.getContext().getMatrices();
            int scale = (y2 - y1) / 2;

            stack.push();
            stack.translate((x2 + x1) / 2F, (y2 + y1) / 2F, 40);
            // Use negative Y scale to flip for UI coordinate system (Y increases downward)
            MatrixStackUtils.scaleStack(stack, scale, -scale, scale);

            this.updateTexture(context.getTransition());
            emitter.lastGlobal.set(0, 0, 0);
            emitter.rotation.identity();
            emitter.renderUI(stack, context.getTransition());

            stack.pop();
        }
    }

    @Override
    public void render3D(FormRenderingContext context)
    {
        this.ensureEmitter(MinecraftClient.getInstance().world, context.transition);

        ParticleEmitter emitter = this.emitter;

        if (emitter != null)
        {
            emitter.setUserVariables(
                this.form.user1.get(),
                this.form.user2.get(),
                this.form.user3.get(),
                this.form.user4.get(),
                this.form.user5.get(),
                this.form.user6.get()
            );

            this.updateTexture(context.getTransition());

            GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;

            gameRenderer.getLightmapTextureManager().enable();
            gameRenderer.getOverlayTexture().setupOverlayColor();

            if (context.modelRenderer)
            {
                emitter.lastGlobal.set(0, 0, 0);
                emitter.rotation.identity();
                emitter.setupCameraProperties(context.camera);
                emitter.cX = 0;
                emitter.cY = 0;
                emitter.cZ = 0;

                if (!BBSRendering.isIrisShadowPass())
                {
                    boolean shadersEnabled = BBSRendering.isIrisShadersEnabled();

                    VertexFormat format = shadersEnabled ? VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL : VertexFormats.POSITION_TEXTURE_COLOR_LIGHT;
                    Supplier<ShaderProgram> shader = shadersEnabled
                        ? this.getShader(context, GameRenderer::getRenderTypeEntityTranslucentProgram, BBSShaders::getPickerBillboardProgram)
                        : this.getShader(context, GameRenderer::getParticleProgram, BBSShaders::getPickerParticlesProgram);

                    Matrix4f currentMatrix = new Matrix4f(context.stack.peek().getPositionMatrix());
                    Matrix4f inverseViewTransform = new Matrix4f()
                        .translate(
                            (float) context.camera.position.x,
                            (float) context.camera.position.y,
                            (float) context.camera.position.z
                        )
                        .mul(new Matrix4f(context.camera.view).invert());
                    Matrix4f localMatrix = new Matrix4f(inverseViewTransform).mul(currentMatrix);

                    context.stack.push();
                    context.stack.loadIdentity();
                    context.stack.multiplyPositionMatrix(localMatrix);

                    emitter.render(format, shader, context.stack, context.overlay, context.getTransition());

                    context.stack.pop();
                }
            }
            else
            {
                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
                Matrix4f positionMatrix = new Matrix4f(context.stack.peek().getPositionMatrix());

                Quaternionf cameraRotation = camera.getRotation();
                Quaternionf inverseCameraRotation = new Quaternionf(cameraRotation).conjugate();
                Matrix4f inverseViewRotation = new Matrix4f().rotation(inverseCameraRotation);
                Matrix4f worldMatrix = new Matrix4f(inverseViewRotation).mul(positionMatrix);
                Vector3f worldSpaceOffset = worldMatrix.getTranslation(new Vector3f());
                Vector3d worldPosition = new Vector3d(
                    worldSpaceOffset.x + camera.getPos().x,
                    worldSpaceOffset.y + camera.getPos().y,
                    worldSpaceOffset.z + camera.getPos().z
                );

                emitter.lastGlobal.set(worldPosition);
                emitter.rotation.set(worldMatrix);

                if (!BBSRendering.isIrisShadowPass())
                {
                    boolean shadersEnabled = BBSRendering.isIrisShadersEnabled();

                    VertexFormat format = shadersEnabled ? VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL : VertexFormats.POSITION_TEXTURE_COLOR_LIGHT;
                    Supplier<ShaderProgram> shader = shadersEnabled
                        ? this.getShader(context, GameRenderer::getRenderTypeEntityTranslucentProgram, BBSShaders::getPickerBillboardProgram)
                        : this.getShader(context, GameRenderer::getParticleProgram, BBSShaders::getPickerParticlesProgram);

                    emitter.setupCameraProperties(camera);

                    context.stack.push();
                    context.stack.loadIdentity();
                    context.stack.multiplyPositionMatrix(new Matrix4f(inverseViewRotation).invert());

                    emitter.render(format, shader, context.stack, context.overlay, context.getTransition());

                    context.stack.pop();
                }
            }

            gameRenderer.getLightmapTextureManager().disable();
            gameRenderer.getOverlayTexture().teardownOverlayColor();
        }
    }

    private void updateTexture(float transition)
    {
        if (this.emitter != null)
        {
            this.emitter.texture = this.form.texture.get();
        }
    }

    @Override
    public void tick(IEntity entity)
    {
        this.ensureEmitter(entity.getWorld(), 0F);

        if (this.emitter != null)
        {
            /* Rewind the emitter if it was paused and resumed in order to make
             * particle effects with once emitter */
            if (this.restart)
            {
                this.emitter.stop();
                this.emitter.start();

                this.restart = false;
            }

            this.emitter.update();
        }
    }
}
