package client.msg.tool;

import client.msg.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import server.NettyServer;

import java.util.List;

/**
 * 如果是服务器类型的消息，要进行处理然后再回传给客户端
 */
public class ServerMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //可读消息字节数小于8，证明不是一个正常消息（正常消息消息类型和消息长度已经占8个字节了）
        if (byteBuf.readableBytes() < 8) {
            return;
        }

        byteBuf.markReaderIndex();

        MsgType msgType = MsgType.values()[byteBuf.readInt()];

        int msgSize = byteBuf.readInt();

        if (byteBuf.readableBytes() < msgSize) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[msgSize];
        byteBuf.readBytes(bytes);

        System.out.println("MsgType : " + msgType + ", MsgSize : " + msgSize);

    }
}
