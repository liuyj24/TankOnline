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
            Tank t = new Tank(x, y, good, dir, tc);
            t.id = id;
            tc.tanks.add(t);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
