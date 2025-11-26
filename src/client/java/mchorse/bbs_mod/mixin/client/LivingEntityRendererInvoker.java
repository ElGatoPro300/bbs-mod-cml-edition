package mchorse.bbs_mod.mixin.client;

import net.minecraft.entity.LivingEntity;

public interface LivingEntityRendererInvoker
{
    default float bbs$getAnimationCounter(LivingEntity entity, float tickDelta)
    {
        return 0F;
    }
}
