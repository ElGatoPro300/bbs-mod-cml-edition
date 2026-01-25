package mchorse.bbs_mod.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor
{
    @Accessor("prevPitch")
    public float getPrevPitch();

    @Accessor("prevPitch")
    public void setPrevPitch(float prevPitch);

    @Accessor("prevYaw")
    public float getPrevYaw();

    @Accessor("prevYaw")
    public void setPrevYaw(float prevYaw);

    @Accessor("prevX")
    public double getPrevX();

    @Accessor("prevX")
    public void setPrevX(double prevX);

    @Accessor("prevY")
    public double getPrevY();

    @Accessor("prevY")
    public void setPrevY(double prevY);

    @Accessor("prevZ")
    public double getPrevZ();

    @Accessor("prevZ")
    public void setPrevZ(double prevZ);
}
