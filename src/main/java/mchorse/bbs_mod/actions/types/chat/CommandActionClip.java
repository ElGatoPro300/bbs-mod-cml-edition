package elgatopro300.bbs_cml.actions.types.chat;

import elgatopro300.bbs_cml.actions.SuperFakePlayer;
import elgatopro300.bbs_cml.actions.types.ActionClip;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.utils.clips.Clip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class CommandActionClip extends ActionClip
{
    public final ValueString command = new ValueString("command", "");

    public CommandActionClip()
    {
        this.add(this.command);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        this.applyPositionRotation(player, replay, tick);

        String command = this.command.get();
        ServerWorld world = player.getServerWorld();
        ServerCommandSource source = actor == null
            ? player.getCommandSource()
            : actor.getCommandSource(world);

        player.getServer().getCommandManager().executeWithPrefix(source, command);
    }

    @Override
    protected Clip create()
    {
        return new CommandActionClip();
    }
}