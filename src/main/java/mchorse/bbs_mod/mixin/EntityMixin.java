package mchorse.bbs_mod.mixin;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.morphing.IMorphProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    public void bbs$onGetDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir)
    {
        if (((Object) this) instanceof IMorphProvider provider)
        {
            Form form = provider.getMorph().getForm();

            if (form != null && form.hitbox.get())
            {
                EntityDimensions dimensions = cir.getReturnValue();
                float height;

                if (((Object) this) instanceof PlayerEntity)
                {
                    PlayerEntity player = (PlayerEntity) (Object) this;
                    height = form.hitboxHeight.get() * (player.isSneaking() ? form.hitboxSneakMultiplier.get() : 1F);
                }
                else
                {
                    height = form.hitboxHeight.get();
                }

                if (dimensions.fixed())
                {
                    cir.setReturnValue(EntityDimensions.fixed(form.hitboxWidth.get(), height));
                }
                else
                {
                    cir.setReturnValue(EntityDimensions.changing(form.hitboxWidth.get(), height));
                }
            }
        }
    }
    
    @Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
    public void bbs$getEyeHeight(EntityPose pose, CallbackInfoReturnable<Float> cir)
    {
        if (((Object) this) instanceof IMorphProvider provider)
        {
            Form form = provider.getMorph().getForm();

            if (form != null && form.hitbox.get())
            {
                float height;

                if (((Object) this) instanceof PlayerEntity)
                {
                    PlayerEntity player = (PlayerEntity) (Object) this;
                    height = form.hitboxHeight.get() * (player.isSneaking() ? form.hitboxSneakMultiplier.get() : 1F);
                }
                else
                {
                    height = form.hitboxHeight.get();
                }

                cir.setReturnValue(form.hitboxEyeHeight.get() * height);
            }
        }
    }
}