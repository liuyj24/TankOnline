import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg {
    int msgType = Msg.MISSILE_DEAD_MESSAGE;
    TankClient tc;
    int tankId;
    int id;

    public MissileDeadMsg(int tankId, int id){
        this.tankId = tankId;
        this.id = id;
    }

    public MissileDeadMsg(TankClient tc){
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(tankId);
            dos.writeInt(id);
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
            int tankId = dis.readInt();
            int id = dis.readInt();
            for(Missile m : tc.missiles){
                if(tankId == tc.myTank.id && id == m.id){
                    m.live = false;
                    tc.explodes.add(new Explode(m.x, m.y, tc));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
