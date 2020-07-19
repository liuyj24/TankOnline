package client.msg.tool;

import client.msg.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码器
 */
public class MsgEncoder extends MessageToByteEncoder<Msg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Msg msg, ByteBuf byteBuf) throws Exception {
        //拿到消息体
        byte[] bytes = msg.toBytes();
        //先写消息类型
        byteBuf.writeInt(msg.getMsgType().ordinal());
        //再写消息长度
        byteBuf.writeInt(bytes.length);
        //最后写具体消息内容
        byteBuf.writeBytes(bytes);
    }
}
