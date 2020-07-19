package client.msg;

import client.bean.Dir;
import client.bean.Tank;
import client.client.TankClient;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 坦克出生消息协议
 */
public class TankNewMsg implements Msg {
    private int id;
    private int x;
    private int y;
    private Dir dir;
    private boolean good;
    private String tankName;

    public TankNewMsg(Tank tank) {
        this.id = tank.getId();
        this.x = tank.getX();
        this.y = tank.getY();
        this.dir = tank.getDir();
        this.good = tank.isGood();
        this.tankName = tank.getName();
    }

    public TankNewMsg() {
    }

    @Override
    public void handle() {
        //如果收到自己的加入消息则忽略
        if (this.id == TankClient.INSTANCE.getMyTank().getId()) {
            return;
        }
        //把新坦克加入到坦克集合中
        Tank newTank = new Tank(tankName, x, y, good, dir, id);
        TankClient.INSTANCE.getTanks().add(newTank);

        //发送自己已存在的消息，让新坦克把自己加入进去
        TankAlreadyExistMsg msg = new TankAlreadyExistMsg(TankClient.INSTANCE.getMyTank());
        TankClient.INSTANCE.getNettyClient().send(msg);
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(55);
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] bytes = null;

        try {
            dos.writeInt(this.id);
            dos.writeInt(this.x);
            dos.writeInt(this.y);
            dos.writeInt(this.dir.ordinal());
            dos.writeBoolean(this.good);
            dos.writeUTF(this.tankName);

            bytes = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    @Override
    public void parse(byte[] bytes) {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            this.id = dis.readInt();
            this.x = dis.readInt();
            this.y = dis.readInt();
            this.dir = Dir.values()[dis.readInt()];
            this.good = dis.readBoolean();
            this.tankName = dis.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TankNew;
    }


}
