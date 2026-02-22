package elgatopro300.bbs_cml.mixin.client;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.client.BBSRendering;
import elgatopro300.bbs_cml.utils.VideoRecorder;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterMixin
{
    // @Shadow
    // public float tickDelta;

    // @Shadow
    // public float lastFrameDuration;

    // @Shadow
    // private long prevTimeMillis;

    private int heldFrames;

    @Inject(method = "beginRenderTick", at = @At("HEAD"), cancellable = true)
    public void onBeginRenderTick(long timeMillis, boolean tick, CallbackInfoReturnable<Integer> info)
    {
        VideoRecorder videoRecorder = BBSModClient.getVideoRecorder();

        if (videoRecorder.isRecording())
        {
            RenderTickCounterAccessor accessor = (RenderTickCounterAccessor) (Object) this;

            if (videoRecorder.getCounter() == 0)
            {
                accessor.setTickDeltaField(0);
            }

            if (this.heldFrames == 0)
            {
                // accessor.setLastFrameDurationField(20F / (float) BBSRendering.getVideoFrameRate());
                accessor.setPrevTimeMillisField(timeMillis);
                
                float tickDelta = accessor.getTickDeltaField();
                tickDelta += 20F / (float) BBSRendering.getVideoFrameRate(); // accessor.getLastFrameDurationField();
                accessor.setTickDeltaField(tickDelta);

                int i = (int) tickDelta;

                accessor.setTickDeltaField(tickDelta - (float) i);

                videoRecorder.serverTicks += i;
                BBSRendering.canRender = true;

                info.setReturnValue(i);
            }
            else
            {
                BBSRendering.canRender = false;

                info.setReturnValue(0);
            }

            this.heldFrames += 1;

            if (this.heldFrames >= BBSSettings.videoSettings.heldFrames.get())
            {
                this.heldFrames = 0;
            }
        }
        else
        {
            this.heldFrames = 0;
        }
    }
}