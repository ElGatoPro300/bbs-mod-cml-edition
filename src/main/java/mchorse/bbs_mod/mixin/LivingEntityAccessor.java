package mchorse.bbs_mod.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor
{
    @Accessor("prevHeadYaw")
    public float getPrevHeadYaw();

    @Accessor("prevHeadYaw")
    public void setPrevHeadYaw(float prevHeadYaw);

    @Accessor("prevBodyYaw")
    public float getPrevBodyYaw();

    @Accessor("prevBodyYaw")
    public void setPrevBodyYaw(float prevBodyYaw);

    @Accessor("bodyYaw")
    public float getBodyYaw();

    @Accessor("bodyYaw")
    public void setBodyYaw(float bodyYaw);
}
