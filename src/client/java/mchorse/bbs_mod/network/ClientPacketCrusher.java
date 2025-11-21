package mchorse.bbs_mod.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload;

public class ClientPacketCrusher extends PacketCrusher
{
    private static CustomPayload.Id<ServerNetwork.BufPayload> idFor(Identifier identifier)
    {
        // En 1.21 usamos CustomPayload.Id directamente desde el Identifier
        return ServerNetwork.idFor(identifier);
    }

    @Override
    protected void sendBuffer(PlayerEntity entity, Identifier identifier, PacketByteBuf buf)
    {
        ClientPlayNetworking.send(ServerNetwork.BufPayload.from(buf, idFor(identifier)));
    }
}