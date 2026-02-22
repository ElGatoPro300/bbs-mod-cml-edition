package mchorse.bbs_mod.mixin.client;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderTickCounter.Dynamic.class)
public interface RenderTickCounterAccessor
{
    @Accessor("dynamicDeltaTicks")
    float getTickDeltaField();

    @Accessor("dynamicDeltaTicks")
    void setTickDeltaField(float tickDelta);

    @Accessor("lastTimeMillis")
    void setPrevTimeMillisField(long prevTimeMillis);
}
