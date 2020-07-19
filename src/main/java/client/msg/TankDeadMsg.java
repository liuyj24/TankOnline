package client.msg;

import client.bean.Tank;
import client.client.TankClient;

import java.io.*;

/**
 * 坦克死亡消息协议
 */
public class TankDeadMsg implements Msg {
    private int tankId;

    public TankDeadMsg() {
    }

    public TankDeadMsg(int tankId) {
        this.tankId = tankId;
    }

    @Override
    public void handle() {
        if (this.tankId == TankClient.INSTANCE.getMyTank().getId()) {
            return;
        }
        for (Tank tank : TankClient.INSTANCE.getTanks()) {
            if (tank.getId() == tankId) {
                tank.setLive(false);
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
        return MsgType.TankDead;
    }
}
