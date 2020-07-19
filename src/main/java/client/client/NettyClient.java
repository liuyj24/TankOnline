package client.client;

import client.msg.Msg;
import client.msg.tool.MsgDecoder;
import client.msg.tool.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static final NettyClient INSTANCE = new NettyClient();

    private Channel channel = null;

    private NettyClient() {
    }

    public void connect() {

        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap bootstrap = new Bootstrap();


        final ChannelFuture future = bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new MsgDecoder());
                pipeline.addLast(new MsgEncoder());
                pipeline.addLast(new ClientHandler());
            }
        })
                .connect("127.0.0.1", 8888);

        //连接成功后保存Channel，用于发送消息
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!future.isSuccess()) {
                    System.out.println("not connected!");
                } else {
                    System.out.println("connected!");
                    channel = future.channel();
                }
            }
        });

        try {
            future.sync().channel().closeFuture().sync();

            System.out.println("connection closed");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void send(Msg msg) {
        channel.writeAndFlush(msg);
    }
}
