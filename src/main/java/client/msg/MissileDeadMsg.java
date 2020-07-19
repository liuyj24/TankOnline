package client.msg;

import client.bean.Dir;
import client.bean.Explode;
import client.bean.Missile;
import client.client.TankClient;

import java.io.*;

/**
 * 子弹死亡消息协议
 */
public class MissileDeadMsg implements Msg {
    private int tankId;
    private int missileId;

    public MissileDeadMsg() {
    }

    public MissileDeadMsg(int tankId, int id) {
        this.tankId = tankId;
        this.missileId = id;
    }

    @Override
    public void handle() {
        for (Missile m : TankClient.INSTANCE.getMissiles()) {
            if (missileId == m.getId()) {
                m.setLive(false);
                TankClient.INSTANCE.getExplodes().add(new Explode(m.getX() - 20, m.getY() - 20));
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
            dos.writeInt(tankId);
            dos.writeInt(missileId);

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
        return MsgType.MissileDead;
    }
}
