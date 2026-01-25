package mchorse.bbs_mod.client.renderer.entity;

import mchorse.bbs_mod.entity.ActorEntity;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

public class ActorEntityRenderState extends LivingEntityRenderState
{
    public ActorEntity actor;
    public float bodyYaw;
    public int overlay;
    public float tickDelta;
}
