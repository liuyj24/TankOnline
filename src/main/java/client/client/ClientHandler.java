package client.client;

import client.msg.GetIdMsg;
import client.msg.Msg;
import client.msg.TankNewMsg;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Msg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        msg.handle();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发送坦克出生信息
        ctx.writeAndFlush(new TankNewMsg(TankClient.INSTANCE.getMyTank()));
    }
}
