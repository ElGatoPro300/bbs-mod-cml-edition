package elgatopro300.bbs_cml.morphing;

import elgatopro300.bbs_cml.forms.forms.Form;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface IEntityCaptureHandler
{
    public Form capture(PlayerEntity player, Entity target);
}
