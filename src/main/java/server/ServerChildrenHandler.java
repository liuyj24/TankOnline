package server;

import client.msg.GetIdMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerChildrenHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyServer.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //判断是否是分配id的消息
//        if (msg instanceof GetIdMsg) {
//            int id = IdCreator.getId();
//            ((GetIdMsg) msg).setId(id);
//            ctx.channel().writeAndFlush(msg);
//            return;
//        }
        NettyServer.clients.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("something wrong with " + ctx);
        NettyServer.clients.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        NettyServer.clients.remove(ctx.channel());
    }
}
