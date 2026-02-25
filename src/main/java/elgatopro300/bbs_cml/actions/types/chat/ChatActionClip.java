package elgatopro300.bbs_cml.actions.types.chat;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.actions.types.ActionClip;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.utils.StringUtils;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ChatActionClip extends ActionClip
{
    public final ValueString message = new ValueString("message", "");

    public ChatActionClip()
    {
        this.add(this.message);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        for (PlayerEntity entity : player.getWorld().getPlayers())
        {
            entity.sendMessage(Text.literal(StringUtils.processColoredText(this.message.get())), false);
        }
    }

    @Override
    protected Clip create()
    {
        return new ChatActionClip();
    }
}