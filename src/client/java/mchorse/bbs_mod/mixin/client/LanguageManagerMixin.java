package elgatopro300.bbs_cml.mixin.client;

import elgatopro300.bbs_cml.BBSModClient;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin
{
    @Inject(method = "reload", at = @At("TAIL"))
    public void onReload(CallbackInfo info)
    {
        BBSModClient.reloadLanguage(BBSModClient.getLanguageKey());
    }
}