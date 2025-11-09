package mchorse.bbs_mod.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerPacketCrusher extends PacketCrusher
{
    @Override
    protected void sendBuffer(PlayerEntity entity, Identifier identifier, PacketByteBuf buf)
    {
        ServerPlayNetworking.send((ServerPlayerEntity) entity, ServerNetwork.BufPayload.from(buf, idFor(identifier)));
    }

    private static net.minecraft.network.packet.CustomPayload.Id<ServerNetwork.BufPayload> idFor(Identifier identifier)
    {
        if (identifier.equals(ServerNetwork.CLIENT_CLICKED_MODEL_BLOCK_PACKET)) return ServerNetwork.C_CLICKED_MODEL_BLOCK_ID;
        if (identifier.equals(ServerNetwork.CLIENT_PLAYER_FORM_PACKET)) return ServerNetwork.C_PLAYER_FORM_ID;
        if (identifier.equals(ServerNetwork.CLIENT_PLAY_FILM_PACKET)) return ServerNetwork.C_PLAY_FILM_ID;
        if (identifier.equals(ServerNetwork.CLIENT_MANAGER_DATA_PACKET)) return ServerNetwork.C_MANAGER_DATA_ID;
        if (identifier.equals(ServerNetwork.CLIENT_STOP_FILM_PACKET)) return ServerNetwork.C_STOP_FILM_ID;
        if (identifier.equals(ServerNetwork.CLIENT_HANDSHAKE)) return ServerNetwork.C_HANDSHAKE_ID;
        if (identifier.equals(ServerNetwork.CLIENT_RECORDED_ACTIONS)) return ServerNetwork.C_RECORDED_ACTIONS_ID;
        if (identifier.equals(ServerNetwork.CLIENT_ANIMATION_STATE_TRIGGER)) return ServerNetwork.C_ANIMATION_STATE_TRIGGER_ID;
        if (identifier.equals(ServerNetwork.CLIENT_CHEATS_PERMISSION)) return ServerNetwork.C_CHEATS_PERMISSION_ID;
        if (identifier.equals(ServerNetwork.CLIENT_SHARED_FORM)) return ServerNetwork.C_SHARED_FORM_ID;
        if (identifier.equals(ServerNetwork.CLIENT_ENTITY_FORM)) return ServerNetwork.C_ENTITY_FORM_ID;
        if (identifier.equals(ServerNetwork.CLIENT_ACTORS)) return ServerNetwork.C_ACTORS_ID;
        if (identifier.equals(ServerNetwork.CLIENT_GUN_PROPERTIES)) return ServerNetwork.C_GUN_PROPERTIES_ID;
        if (identifier.equals(ServerNetwork.CLIENT_PAUSE_FILM)) return ServerNetwork.C_PAUSE_FILM_ID;
        if (identifier.equals(ServerNetwork.CLIENT_SELECTED_SLOT)) return ServerNetwork.C_SELECTED_SLOT_ID;
        if (identifier.equals(ServerNetwork.CLIENT_ANIMATION_STATE_MODEL_BLOCK_TRIGGER)) return ServerNetwork.C_ANIMATION_STATE_MODEL_BLOCK_TRIGGER_ID;
        if (identifier.equals(ServerNetwork.CLIENT_REFRESH_MODEL_BLOCKS)) return ServerNetwork.C_REFRESH_MODEL_BLOCKS_ID;
        throw new IllegalArgumentException("Unknown S2C identifier: " + identifier);
    }
}