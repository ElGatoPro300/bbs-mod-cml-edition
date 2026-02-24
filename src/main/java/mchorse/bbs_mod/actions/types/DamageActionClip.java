package elgatopro300.bbs_cml.actions.types;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.settings.values.numeric.ValueFloat;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.entity.LivingEntity;

public class DamageActionClip extends ActionClip
{
    public final ValueFloat damage = new ValueFloat("damage", 0F);

    public DamageActionClip()
    {
        super();

        this.add(this.damage);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        float damage = this.damage.get();

        if (damage <= 0F)
        {
            return;
        }

        this.applyPositionRotation(player, replay, tick);

        if (actor != null)
        {
            actor.damage(player.getWorld().getDamageSources().mobAttack(player), damage);
        }
    }

    @Override
    protected Clip create()
    {
        return new DamageActionClip();
    }
}