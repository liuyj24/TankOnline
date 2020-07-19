package client.msg.tool;

import client.msg.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 消息解码器
 */
public class MsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
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

        Msg msg = null;

        switch (msgType) {
            case TankAlreadyExist:
                msg = new TankAlreadyExistMsg();
                break;
            case TankDead:
                msg = new TankDeadMsg();
                break;
            case TankMove:
                msg = new TankMoveMsg();
                break;
            case TankNew:
                msg = new TankNewMsg();
                break;
            case TankReduceBlood:
                msg = new TankReduceBloodMsg();
                break;
            case MissileDead:
                msg = new MissileDeadMsg();
                break;
            case MissileNew:
                msg = new MissileNewMsg();
                break;
        }
        msg.parse(bytes);
        list.add(msg);
    }
}
