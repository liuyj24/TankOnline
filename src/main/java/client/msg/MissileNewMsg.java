package client.msg;

import client.bean.Dir;
import client.bean.Missile;
import client.client.TankClient;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 子弹产生消息协议
 */
public class MissileNewMsg implements Msg {
    private int tankId, missileId, x, y;
    private Dir dir;
    private boolean good;

    public MissileNewMsg() {
    }

    public MissileNewMsg(Missile m) {
        this.tankId = m.getTankId();
        this.missileId = m.getId();
        this.x = m.getX();
        this.y = m.getY();
        this.dir = m.getDir();
        this.good = m.isGood();
    }

    @Override
    public void handle() {
        //如果是自己发的消息则忽略
        if (this.tankId == TankClient.INSTANCE.getMyTank().getId()) {
            return;
        }
        Missile missile = new Missile(tankId, x, y, good, dir, missileId);
        TankClient.INSTANCE.getMissiles().add(missile);
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(50);
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] bytes = null;
        try {
            dos.writeInt(this.tankId);
            dos.writeInt(this.missileId);
            dos.writeInt(this.x);
            dos.writeInt(this.y);
            dos.writeInt(this.dir.ordinal());
            dos.writeBoolean(this.good);

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
            this.tankId = dis.readInt();
            this.missileId = dis.readInt();
            this.x = dis.readInt();
            this.y = dis.readInt();
            this.dir = Dir.values()[dis.readInt()];
            this.good = dis.readBoolean();

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
        return MsgType.MissileNew;
    }
}
