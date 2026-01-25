package mchorse.bbs_mod.client.renderer.entity;

import mchorse.bbs_mod.entity.GunProjectileEntity;
import mchorse.bbs_mod.items.GunProperties;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class GunProjectileEntityRenderState extends EntityRenderState
{
    public GunProjectileEntity projectile;
    public GunProperties properties;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    public float age;
    public float tickDelta;
}
