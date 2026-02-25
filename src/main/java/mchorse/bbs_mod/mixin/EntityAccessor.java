package mchorse.bbs_mod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor
{
    @Accessor("dimensions")
    void setDimensions(EntityDimensions dimensions);

    @Accessor("standingEyeHeight")
    void setStandingEyeHeight(float height);

    @Invoker("refreshPosition")
    void callRefreshPosition();
}
