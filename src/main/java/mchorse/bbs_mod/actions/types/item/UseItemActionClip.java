package elgatopro300.bbs_cml.actions.types.item;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.items.GunItem;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class UseItemActionClip extends ItemActionClip
{
    public final elgatopro300.bbs_cml.settings.values.numeric.ValueInt useTicks = new elgatopro300.bbs_cml.settings.values.numeric.ValueInt("use_ticks", 0, 0, Integer.MAX_VALUE);

    public UseItemActionClip()
    {
        super();

        this.add(this.useTicks);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        Hand hand = this.hand.get() ? Hand.MAIN_HAND : Hand.OFF_HAND;

        GunItem.actor = actor;

        this.applyPositionRotation(player, replay, tick);
        ItemStack copy = this.itemStack.get().copy();
        int maxUseTime = copy.getMaxUseTime(player);
        int used = this.useTicks.get();

        player.setStackInHand(hand, copy);
        copy.use(player.world, player, hand);

        if (used > 0 && maxUseTime > 0)
        {
            int remaining = Math.max(0, maxUseTime - used);
            copy.onStoppedUsing(player.world, player, remaining);
            player.stopUsingItem();
        }

        player.setStackInHand(hand, ItemStack.EMPTY);

        GunItem.actor = null;
    }

    @Override
    protected Clip create()
    {
        return new UseItemActionClip();
    }
}
