package elgatopro300.bbs_cml.mixin;

import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimbAnimator.class)
public interface LimbAnimatorAccessor
{
    @Accessor("lastSpeed")
    public float getLastSpeed();

    @Accessor("lastSpeed")
    public void setLastSpeed(float v);

    @Accessor("animationProgress")
    public void setAnimationProgress(float v);
}
