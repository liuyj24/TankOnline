package client.msg;

import client.bean.Dir;
import client.bean.Tank;
import client.client.TankClient;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 坦克移动消息协议
 */
public class TankMoveMsg implements Msg {
    private int id;
    private int x, y;
    private Dir dir;
    private Dir ptDir;
    private TankClient tc;

    public TankMoveMsg() {
    }

    public TankMoveMsg(int id, int x, int y, Dir dir, Dir ptDir) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.ptDir = ptDir;
    }

    @Override
    public void handle() {
        if (this.id == TankClient.INSTANCE.getMyTank().getId()) {
            return;
        }
        for (Tank tank : TankClient.INSTANCE.getTanks()) {
            if (tank.getId() == this.id) {
                tank.setDir(this.dir);
                tank.setPtDir(this.ptDir);
                tank.setX(this.x);
                tank.setY(this.y);
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
            dos.writeInt(id);
            dos.writeInt(dir.ordinal());
            dos.writeInt(ptDir.ordinal());
            dos.writeInt(x);
            dos.writeInt(y);

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
            this.dir = Dir.values()[dis.readInt()];
            this.ptDir = Dir.values()[dis.readInt()];
            this.x = dis.readInt();
            this.y = dis.readInt();

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
        return MsgType.TankMove;
    }
}
