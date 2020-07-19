package client.msg;

import client.bean.Dir;
import client.bean.Explode;
import client.bean.Missile;
import client.bean.Tank;
import client.client.TankClient;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankReduceBloodMsg implements Msg {
    private int tankId;
    private Missile m;
    private int explodeX, explodeY;

    public TankReduceBloodMsg() {
    }

    public TankReduceBloodMsg(int tankId, Missile m) {
        this.tankId = tankId;
        this.m = m;
    }


    @Override
    public void handle() {
        if (this.tankId == TankClient.INSTANCE.getMyTank().getId()) {
            return;
        }
        //产生一个爆炸
        Explode explode = new Explode(this.explodeX, this.explodeY);
        TankClient.INSTANCE.getExplodes().add(explode);

        //让目标坦克扣血
        for (Tank tank : TankClient.INSTANCE.getTanks()) {
            if (tank.getId() == this.tankId) {
                tank.setBlood(tank.getBlood() - 20);
                break;
            }
        }
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(50);
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] bytes = null;
        try {
            //扣血坦克的id
            dos.writeInt(tankId);
            //爆炸产生的位置
            dos.writeInt(m.getX() - 20);
            dos.writeInt(m.getY() - 20);

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
            this.explodeX = dis.readInt();
            this.explodeY = dis.readInt();

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
        return MsgType.TankReduceBlood;
    }
}
