package elgatopro300.bbs_cml.network;

import net.minecraft.network.PacketByteBuf;

public interface IBufferReceiver
{
    public void receiveBuffer(byte[] bytes, PacketByteBuf buf);
}