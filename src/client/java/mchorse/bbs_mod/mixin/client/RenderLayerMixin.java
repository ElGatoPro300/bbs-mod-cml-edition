package elgatopro300.bbs_cml.mixin.client;

import elgatopro300.bbs_cml.forms.CustomVertexConsumerProvider;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.RenderLayer$MultiPhase")
public class RenderLayerMixin
{
    @Inject(method = "draw", at = @At("HEAD"))
    public void onDraw(BuiltBuffer buffer, CallbackInfo info)
    {
        CustomVertexConsumerProvider.drawLayer((RenderLayer) (Object) this);
    }
}