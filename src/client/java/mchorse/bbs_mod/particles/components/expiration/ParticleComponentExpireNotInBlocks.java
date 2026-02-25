package elgatopro300.bbs_cml.particles.components.expiration;

import elgatopro300.bbs_cml.particles.components.IComponentParticleUpdate;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;
import net.minecraft.block.BlockState;

public class ParticleComponentExpireNotInBlocks extends ParticleComponentExpireBlocks implements IComponentParticleUpdate
{
    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        if (particle.isDead() || emitter.world == null)
        {
            return;
        }

        BlockState current = this.getBlock(emitter, particle);

        for (String block : this.blocks)
        {
            if (current.getBlock().getRegistryEntry().registryKey().getValue().toString().equals(block))
            {
                return;
            }
        }

        particle.setDead();
    }
}