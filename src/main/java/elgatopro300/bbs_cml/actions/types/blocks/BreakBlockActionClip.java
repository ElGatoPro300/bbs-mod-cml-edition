package elgatopro300.bbs_cml.actions.types.blocks;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class BreakBlockActionClip extends BlockActionClip
{
    public final ValueInt progress = new ValueInt("progress", 0);

    public BreakBlockActionClip()
    {
        super();

        this.add(this.progress);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        player.getWorld().setBlockBreakingInfo(player.getId(), new BlockPos(this.x.get(), this.y.get(), this.z.get()), this.progress.get());
    }

    @Override
    protected Clip create()
    {
        return new BreakBlockActionClip();
    }
}