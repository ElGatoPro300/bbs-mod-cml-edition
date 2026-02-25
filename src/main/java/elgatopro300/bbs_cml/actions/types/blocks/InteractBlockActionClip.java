package elgatopro300.bbs_cml.actions.types.blocks;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.actions.types.ActionClip;
import elgatopro300.bbs_cml.actions.values.ValueBlockHitResult;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class InteractBlockActionClip extends ActionClip
{
    public final ValueBlockHitResult hit = new ValueBlockHitResult("hit");
    public final ValueBoolean hand = new ValueBoolean("hand", true);

    public InteractBlockActionClip()
    {
        super();

        this.add(this.hit);
        this.add(this.hand);
    }

    @Override
    public void shift(double dx, double dy, double dz)
    {
        super.shift(dx, dy, dz);

        this.hit.shift(dx, dy, dz);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        this.applyPositionRotation(player, replay, tick);

        BlockHitResult result = this.hit.getHitResult();

        player.getWorld().getBlockState(result.getBlockPos()).onUse(player.getWorld(), player, result);
    }

    @Override
    protected Clip create()
    {
        return new InteractBlockActionClip();
    }
}