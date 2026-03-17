package me.m56738.smoothcoasters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

public class PacketInterceptor extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CustomPayloadS2CPacket) {
            CustomPayloadS2CPacket packet = (CustomPayloadS2CPacket) msg;
            if (SmoothCoasters.getInstance().handlePacket(packet)) {
                return;
            }
        }
        super.channelRead(ctx, msg);
    }
}
