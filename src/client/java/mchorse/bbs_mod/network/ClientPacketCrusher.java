package mchorse.bbs_mod.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientPacketCrusher extends PacketCrusher
{
    @Override
    protected void sendBuffer(PlayerEntity entity, Identifier identifier, PacketByteBuf buf)
    {
        ClientPlayNetworking.send(ServerNetwork.BufPayload.from(buf, idFor(identifier)));
    }

    private static net.minecraft.network.packet.CustomPayload.Id<ServerNetwork.BufPayload> idFor(Identifier identifier)
    {
        // Map all C2S identifiers used by ClientNetwork to typed payload IDs
        if (identifier.equals(ServerNetwork.SERVER_MODEL_BLOCK_FORM_PACKET)) return ServerNetwork.S_MODEL_BLOCK_FORM_ID;
        if (identifier.equals(ServerNetwork.SERVER_MODEL_BLOCK_TRANSFORMS_PACKET)) return ServerNetwork.S_MODEL_BLOCK_TRANSFORMS_ID;
        if (identifier.equals(ServerNetwork.SERVER_PLAYER_FORM_PACKET)) return ServerNetwork.S_PLAYER_FORM_ID;
        if (identifier.equals(ServerNetwork.SERVER_MANAGER_DATA_PACKET)) return ServerNetwork.S_MANAGER_DATA_ID;
        if (identifier.equals(ServerNetwork.SERVER_FILM_DATA_SYNC)) return ServerNetwork.S_FILM_DATA_SYNC_ID;
        if (identifier.equals(ServerNetwork.SERVER_ACTION_RECORDING)) return ServerNetwork.S_ACTION_RECORDING_ID;
        if (identifier.equals(ServerNetwork.SERVER_TOGGLE_FILM)) return ServerNetwork.S_TOGGLE_FILM_ID;
        if (identifier.equals(ServerNetwork.SERVER_ACTION_CONTROL)) return ServerNetwork.S_ACTION_CONTROL_ID;
        if (identifier.equals(ServerNetwork.SERVER_PLAYER_TP)) return ServerNetwork.S_PLAYER_TP_ID;
        if (identifier.equals(ServerNetwork.SERVER_ANIMATION_STATE_TRIGGER)) return ServerNetwork.S_ANIMATION_STATE_TRIGGER_ID;
        if (identifier.equals(ServerNetwork.SERVER_SHARED_FORM)) return ServerNetwork.S_SHARED_FORM_ID;
        if (identifier.equals(ServerNetwork.SERVER_ZOOM)) return ServerNetwork.S_ZOOM_ID;
        if (identifier.equals(ServerNetwork.SERVER_PAUSE_FILM)) return ServerNetwork.S_PAUSE_FILM_ID;
        throw new IllegalArgumentException("Unknown C2S identifier: " + identifier);
    }
}