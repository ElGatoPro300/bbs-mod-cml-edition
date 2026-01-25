package mchorse.bbs_mod.forms.entities;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.morphing.Morph;
import mchorse.bbs_mod.utils.AABB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import mchorse.bbs_mod.mixin.EntityAccessor;
import mchorse.bbs_mod.mixin.LimbAnimatorAccessor;
import mchorse.bbs_mod.mixin.LivingEntityAccessor;

public class MCEntity implements IEntity
{
    private Entity mcEntity;

    private float prevPrevBodyYaw;
    private Vec3d lastVelocity = Vec3d.ZERO;

    private float[] extraVariables = new float[10];
    private float[] prevExtraVariables = new float[10];

    public MCEntity(Entity mcEntity)
    {
        this.mcEntity = mcEntity;
    }

    public Entity getMcEntity()
    {
        return this.mcEntity;
    }

    @Override
    public void setWorld(World world)
    {}

    @Override
    public World getWorld()
    {
        return this.mcEntity.getWorld();
    }

    @Override
    public Form getForm()
    {
        Morph morph = Morph.getMorph(this.mcEntity);

        return morph == null ? null : morph.getForm();
    }

    @Override
    public void setForm(Form form)
    {
        Morph morph = Morph.getMorph(this.mcEntity);

        if (morph != null)
        {
            morph.setForm(form);
        }
    }

    @Override
    public ItemStack getEquipmentStack(EquipmentSlot slot)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.getEquippedStack(slot);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setEquipmentStack(EquipmentSlot slot, ItemStack stack)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            living.equipStack(slot, stack == null ? ItemStack.EMPTY : stack);
        }
    }

    @Override
    public int getSelectedSlot()
    {
        if (this.mcEntity instanceof PlayerEntity player)
        {
            return player.getInventory().getSelectedSlot();
        }

        return 0;
    }

    @Override
    public boolean isSneaking()
    {
        return this.mcEntity.isSneaking();
    }

    @Override
    public void setSneaking(boolean sneaking)
    {
        this.mcEntity.setSneaking(sneaking);
    }

    @Override
    public boolean isSprinting()
    {
        return this.mcEntity.isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting)
    {
        this.mcEntity.setSprinting(sprinting);
    }

    @Override
    public boolean isOnGround()
    {
        return this.mcEntity.isOnGround();
    }

    @Override
    public void setOnGround(boolean ground)
    {
        this.mcEntity.setOnGround(ground);
    }

    @Override
    public void swingArm()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            living.swingHand(Hand.MAIN_HAND);
        }
    }

    @Override
    public float getHandSwingProgress(float tickDelta)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.getHandSwingProgress(tickDelta);
        }

        return 0F;
    }

    @Override
    public int getAge()
    {
        return this.mcEntity.age;
    }

    @Override
    public void setAge(int ticks)
    {
        this.mcEntity.age = ticks;
    }

    @Override
    public float getFallDistance()
    {
        return (float) this.mcEntity.fallDistance;
    }

    @Override
    public void setFallDistance(float fallDistance)
    {
        this.mcEntity.fallDistance = fallDistance;
    }

    @Override
    public int getHurtTimer()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.hurtTime;
        }

        return 0;
    }

    @Override
    public void setHurtTimer(int hurtTimer)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            living.hurtTime = hurtTimer;
        }
    }

    @Override
    public double getX()
    {
        return this.mcEntity.getX();
    }

    @Override
    public double getPrevX()
    {
        return ((EntityAccessor) this.mcEntity).getPrevX();
    }

    @Override
    public void setPrevX(double x)
    {
        ((EntityAccessor) this.mcEntity).setPrevX(x);
    }

    @Override
    public double getY()
    {
        return this.mcEntity.getY();
    }

    @Override
    public double getPrevY()
    {
        return ((EntityAccessor) this.mcEntity).getPrevY();
    }

    @Override
    public void setPrevY(double y)
    {
        ((EntityAccessor) this.mcEntity).setPrevY(y);
    }

    @Override
    public double getZ()
    {
        return this.mcEntity.getZ();
    }

    @Override
    public double getPrevZ()
    {
        return ((EntityAccessor) this.mcEntity).getPrevZ();
    }

    @Override
    public void setPrevZ(double z)
    {
        ((EntityAccessor) this.mcEntity).setPrevZ(z);
    }

    @Override
    public void setPosition(double x, double y, double z)
    {
        this.mcEntity.setPosition(x, y, z);
    }

    @Override
    public double getEyeHeight()
    {
        return this.mcEntity.getEyeHeight(this.mcEntity.getPose());
    }

    @Override
    public Vec3d getVelocity()
    {
        return this.mcEntity.getVelocity();
    }

    @Override
    public void setVelocity(float x, float y, float z)
    {
        this.mcEntity.setVelocity(x, y, z);
    }

    @Override
    public float getYaw()
    {
        return this.mcEntity.getYaw();
    }

    @Override
    public float getPrevYaw()
    {
        return ((EntityAccessor) this.mcEntity).getPrevYaw();
    }

    @Override
    public void setYaw(float yaw)
    {
        this.mcEntity.setYaw(yaw);
    }

    @Override
    public void setPrevYaw(float prevYaw)
    {
        ((EntityAccessor) this.mcEntity).setPrevYaw(prevYaw);
    }

    @Override
    public float getHeadYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.getHeadYaw();
        }

        return this.mcEntity.getYaw();
    }

    @Override
    public float getPrevHeadYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return ((LivingEntityAccessor) living).getPrevHeadYaw();
        }

        return ((EntityAccessor) this.mcEntity).getPrevYaw();
    }

    @Override
    public void setHeadYaw(float headYaw)
    {
        this.mcEntity.setHeadYaw(headYaw);
    }

    @Override
    public void setPrevHeadYaw(float prevHeadYaw)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            ((LivingEntityAccessor) living).setPrevHeadYaw(prevHeadYaw);
        }
    }

    @Override
    public float getPitch()
    {
        return this.mcEntity.getPitch();
    }

    @Override
    public float getPrevPitch()
    {
        return ((EntityAccessor) this.mcEntity).getPrevPitch();
    }

    @Override
    public void setPitch(float pitch)
    {
        this.mcEntity.setPitch(pitch);
    }

    @Override
    public void setPrevPitch(float prevPitch)
    {
        ((EntityAccessor) this.mcEntity).setPrevPitch(prevPitch);
    }

    @Override
    public float getBodyYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return ((LivingEntityAccessor) living).getBodyYaw();
        }

        return this.getHeadYaw();
    }

    @Override
    public float getPrevBodyYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return ((LivingEntityAccessor) living).getPrevBodyYaw();
        }

        return this.getPrevHeadYaw();
    }

    @Override
    public float getPrevPrevBodyYaw()
    {
        return this.prevPrevBodyYaw;
    }

    @Override
    public void setBodyYaw(float bodyYaw)
    {
        this.mcEntity.setBodyYaw(bodyYaw);
    }

    @Override
    public void setPrevBodyYaw(float prevBodyYaw)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            ((LivingEntityAccessor) living).setPrevBodyYaw(prevBodyYaw);
        }
    }

    @Override
    public void setPrevPrevBodyYaw(float prevPrevBodyYaw)
    {
        this.prevPrevBodyYaw = prevPrevBodyYaw;
    }

    @Override
    public float[] getExtraVariables()
    {
        return this.extraVariables;
    }

    @Override
    public float[] getPrevExtraVariables()
    {
        return this.prevExtraVariables;
    }

    @Override
    public AABB getPickingHitbox()
    {
        float w = this.mcEntity.getWidth();
        float h = this.mcEntity.getHeight();

        return new AABB(
            this.getX() - w / 2, this.getY(), this.getZ() - w / 2,
            w, h, w
        );
    }

    @Override
    public void update()
    {
        this.lastVelocity = this.mcEntity.getVelocity();
        this.prevPrevBodyYaw = this.getPrevBodyYaw();

        for (int i = 0; i < this.extraVariables.length; i++)
        {
            this.prevExtraVariables[i] = this.extraVariables[i];
        }
    }

    @Override
    public LimbAnimator getLimbAnimator()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.limbAnimator;
        }

        return null;
    }

    @Override
    public float getLimbPos(float tickDelta)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            LimbAnimatorAccessor animator = (LimbAnimatorAccessor) living.limbAnimator;

            return MathHelper.lerp(tickDelta, animator.getPrevPos(), animator.getPos());
        }

        return 0F;
    }

    @Override
    public float getLimbSpeed(float tickDelta)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.limbAnimator.getSpeed();
        }

        return 0F;
    }

    @Override
    public float getLeaningPitch(float tickDelta)
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.getLeaningPitch(tickDelta);
        }

        return 0F;
    }

    @Override
    public boolean isTouchingWater()
    {
        return this.mcEntity.isTouchingWater();
    }

    @Override
    public EntityPose getEntityPose()
    {
        return this.mcEntity.getPose();
    }

    @Override
    public int getRoll()
    {
        return 0;
    }

    @Override
    public boolean isFallFlying()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.isGliding();
        }

        return false;
    }

    @Override
    public Vec3d getRotationVec(float transition)
    {
        return this.mcEntity.getRotationVec(transition);
    }

    @Override
    public Vec3d lerpVelocity(float transition)
    {
        return this.lastVelocity.lerp(this.mcEntity.getVelocity(), transition);
    }

    @Override
    public boolean isUsingRiptide()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.isUsingRiptide();
        }

        return false;
    }
}