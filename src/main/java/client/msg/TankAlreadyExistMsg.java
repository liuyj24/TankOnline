package client.msg;

import client.bean.Dir;
import client.bean.Tank;
import client.client.TankClient;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 旧坦克向新坦克发送消息的协议
 */
public class TankAlreadyExistMsg implements Msg {

    private int id, x, y;
    private Dir dir;
    private boolean good;
    private String tankName;

    public TankAlreadyExistMsg() {
    }

    public TankAlreadyExistMsg(Tank tank) {
        this.id = tank.getId();
        this.x = tank.getX();
        this.y = tank.getY();
        this.dir = tank.getDir();
        this.good = tank.isGood();
        this.tankName = tank.getName();
    }

    @Override
    public void handle() {
        //如果是自己发的消息则忽略
        if (this.id == TankClient.INSTANCE.getMyTank().getId()) {
            return;
        }
        //先查看坦克集合中该坦克是否存在
        boolean exist = false;
        for (Tank t : TankClient.INSTANCE.getTanks()) {
            if (this.id == t.getId()) {
                exist = true;
                break;
            }
        }
        //如果不存在则加入到坦克集合中
        if (!exist) {
            Tank existTank = new Tank(tankName, x, y, good, dir, id);
            TankClient.INSTANCE.getTanks().add(existTank);
        }
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(50);
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
        return MsgType.TankAlreadyExist;
    }
}
