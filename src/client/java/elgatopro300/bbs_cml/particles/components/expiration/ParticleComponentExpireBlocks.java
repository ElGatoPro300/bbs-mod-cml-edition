package elgatopro300.bbs_cml.particles.components.expiration;

import elgatopro300.bbs_cml.data.DataStorageUtils;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.emitter.Particle;
import elgatopro300.bbs_cml.particles.emitter.ParticleEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class ParticleComponentExpireBlocks extends ParticleComponentBase
{
    public List<String> blocks = new ArrayList<>();

    @Override
    public BaseType toData()
    {
        return DataStorageUtils.stringListToData(this.blocks);
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isList())
        {
            return super.fromData(data, parser);
        }

        this.blocks = DataStorageUtils.stringListFromData(data);

        return super.fromData(data, parser);
    }

    public BlockState getBlock(ParticleEmitter emitter, Particle particle)
    {
        if (emitter.world == null)
        {
            return null;
        }

        Vector3d position = particle.getGlobalPosition(emitter);

        return emitter.world.getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z));
    }
}