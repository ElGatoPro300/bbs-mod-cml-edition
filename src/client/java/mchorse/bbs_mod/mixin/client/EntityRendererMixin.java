package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.ducks.IEntityRenderState;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState>
{
    @Inject(method = "updateRenderState", at = @At("HEAD"))
    public void bbs$updateRenderState(T entity, S state, float tickDelta, CallbackInfo ci)
    {
        ((IEntityRenderState) state).bbs$setEntity(entity);
    }
}
