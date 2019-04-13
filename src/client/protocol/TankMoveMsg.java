package client.protocol;

import client.bean.Dir;
import client.bean.Tank;
import client.client.TankClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg {
    int msgType = Msg.TANK_MOVE_MSG;
    int id;
    int x, y;
    Dir dir;
    Dir ptDir;
    TankClient tc;

    public TankMoveMsg(int id, int x, int y, Dir dir, Dir ptDir){
        this.id = id;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.ptDir = ptDir;
    }

    public TankMoveMsg(TankClient tc){
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(30);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);
            dos.writeInt(dir.ordinal());
            dos.writeInt(ptDir.ordinal());
            dos.writeInt(x);
            dos.writeInt(y);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf = baos.toByteArray();
        try{
            DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, UDP_Port));
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dis) {
        try{
            int id = dis.readInt();
            if(id == this.tc.myTank.id){
                return;
            }
            Dir dir = Dir.values()[dis.readInt()];
            Dir ptDir = Dir.values()[dis.readInt()];
            int x = dis.readInt();
            int y = dis.readInt();
            for(Tank t : tc.tanks){
                if(t.id == id){
                    t.dir = dir;
                    t.ptDir = ptDir;
                    t.x = x;
                    t.y = y;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
