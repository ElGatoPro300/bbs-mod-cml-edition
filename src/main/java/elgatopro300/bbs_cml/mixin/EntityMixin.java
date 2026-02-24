package elgatopro300.bbs_cml.mixin;

import elgatopro300.bbs_cml.entity.IEntityFormProvider;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.morphing.IMorphProvider;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
    public void onIsCollidable(CallbackInfoReturnable<Boolean> info)
    {
        if ((Object) this instanceof IMorphProvider provider)
        {
            Form form = provider.getMorph().getForm();

            if (form != null && form.hitbox.get())
            {
                info.setReturnValue(true);
            }
        }
        else if ((Object) this instanceof IEntityFormProvider provider)
        {
            Form form = provider.getForm();

            if (form != null && form.hitbox.get())
            {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void onIsPushable(CallbackInfoReturnable<Boolean> info)
    {
        if ((Object) this instanceof IMorphProvider provider)
        {
            Form form = provider.getMorph().getForm();

            if (form != null && form.hitbox.get())
            {
                info.setReturnValue(false);
            }
        }
        else if ((Object) this instanceof IEntityFormProvider provider)
        {
            Form form = provider.getForm();

            if (form != null && form.hitbox.get())
            {
                info.setReturnValue(false);
            }
        }
    }
}
