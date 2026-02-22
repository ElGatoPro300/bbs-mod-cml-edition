package elgatopro300.bbs_cml.actions.types.blocks;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.settings.values.mc.ValueBlockState;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockActionClip extends BlockActionClip
{
    public final ValueBlockState state = new ValueBlockState("state");
    public final ValueBoolean drop = new ValueBoolean("drop", false);

    public PlaceBlockActionClip()
    {
        super();

        this.add(this.state);
        this.add(this.drop);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        BlockPos pos = new BlockPos(this.x.get(), this.y.get(), this.z.get());

        if (this.state.get().getBlock() == Blocks.AIR)
        {
            player.world.breakBlock(pos, this.drop.get());
        }
        else
        {
            player.world.setBlockState(pos, this.state.get());
        }
    }

    @Override
    protected Clip create()
    {
        return new PlaceBlockActionClip();
    }
}