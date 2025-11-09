package mchorse.bbs_mod.mixin;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.morphing.IMorphProvider;
import mchorse.bbs_mod.morphing.Morph;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For some unknown reason to me, if these methods are used in {@link PlayerEntityMorphMixin}
 * then the world will be locked for some reason... by extracting write/read NBT method to
 * a separate mixin fixes it...
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info)
    {
        if (this instanceof IMorphProvider provider)
        {
            nbt.put("BBSMorph", provider.getMorph().toNbt());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info)
    {
        if (this instanceof IMorphProvider provider)
        {
            if (nbt.contains("BBSMorph"))
            {
                provider.getMorph().fromNbt(nbt.getCompound("BBSMorph"));
            }
        }
    }

    
}