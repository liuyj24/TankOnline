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

public class TankNewMsg implements Msg{
    int msgType = Msg.TANK_NEW_MSG;
    Tank tank;
    TankClient tc;

    public TankNewMsg(Tank tank){
        this.tank = tank;
        this.tc = tc;
    }

    public TankNewMsg(TankClient tc){
        this.tc = tc;
        tank = tc.myTank;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port){
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(tank.id);
            dos.writeInt(tank.x);
            dos.writeInt(tank.y);
            dos.writeInt(tank.dir.ordinal());
            dos.writeBoolean(tank.good);

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

    public void parse(DataInputStream dis){
        try{
            int id = dis.readInt();
            if(id == this.tc.myTank.id){
                return;
            }

            int x = dis.readInt();
            int y = dis.readInt();
            Dir dir = Dir.values()[dis.readInt()];
            boolean good = dis.readBoolean();

            //接收到别人的新信息, 判断别人的坦克是否已将加入到tanks集合中
            boolean exist = false;
            for (Tank t : tc.tanks){
                if(id == t.id){
                    exist = true;
                    break;
                }
            }
            if(!exist) {//当判断到接收的新坦克不存在已有集合才加入到集合.
                TankNewMsg msg = new TankNewMsg(tc);
                tc.nc.send(msg);//加入一辆新坦克后要把自己的信息也发送出去.
                System.out.println("send self msg");

                Tank t = new Tank(x, y, good, dir, tc);
                t.id = id;
                tc.tanks.add(t);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
