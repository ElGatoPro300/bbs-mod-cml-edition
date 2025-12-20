package mchorse.bbs_mod.bridge;

import net.minecraft.entity.LivingEntity;

public interface EntityRenderStateBridge
{
    void bbs$setEntity(LivingEntity entity);
    LivingEntity bbs$getEntity();

    void bbs$setTickDelta(float tickDelta);
    float bbs$getTickDelta();
}
