package elgatopro300.bbs_cml.mixin.client;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.camera.controller.ICameraController;
import elgatopro300.bbs_cml.camera.controller.PlayCameraController;
import elgatopro300.bbs_cml.client.BBSRendering;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void render(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo info)
    {
        ICameraController current = BBSModClient.getCameraController().getCurrent();

        if (current instanceof PlayCameraController)
        {
            BBSRendering.onRenderBeforeScreen();

            info.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onRenderEnd(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo info)
    {
        BBSRendering.onRenderBeforeScreen();
    }
}