package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.bridge.EntityRenderStateBridge;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateBridge
{
    @Unique
    private LivingEntity bbs$entity;

    @Unique
    private float bbs$tickDelta;

    @Override
    public void bbs$setEntity(LivingEntity entity)
    {
        this.bbs$entity = entity;
    }

    @Override
    public LivingEntity bbs$getEntity()
    {
        return this.bbs$entity;
    }

    @Override
    public void bbs$setTickDelta(float tickDelta)
    {
        this.bbs$tickDelta = tickDelta;
    }

    @Override
    public float bbs$getTickDelta()
    {
        return this.bbs$tickDelta;
    }
}
