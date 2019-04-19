package client.protocol;

import client.bean.Explode;
import client.bean.Missile;
import client.client.TankClient;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 子弹死亡消息协议
 */
public class MissileDeadMsg implements Msg {
    private int msgType = Msg.MISSILE_DEAD_MSG;
    private TankClient tc;
    private int tankId;
    private int id;

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
            for(Missile m : tc.getMissiles()){
                if(tankId == tc.getMyTank().getId() && id == m.getId()){
                    m.setLive(false);
                    tc.getExplodes().add(new Explode(m.getX() - 20, m.getY() - 20, tc));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
