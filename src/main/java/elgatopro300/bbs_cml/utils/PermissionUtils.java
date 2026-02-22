package elgatopro300.bbs_cml.utils;

import elgatopro300.bbs_cml.BBSMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.authlib.GameProfile;

public class PermissionUtils
{
    public static boolean arePanelsAllowed(MinecraftServer server, ServerPlayerEntity player)
    {
        boolean ruleValue = server.getOverworld().getGameRules().getValue(BBSMod.BBS_EDITING_RULE);
        boolean isOp = server.isSingleplayer();
        boolean allowed = ruleValue || isOp;

        return allowed;
    }
}
