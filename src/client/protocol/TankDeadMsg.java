package client.protocol;

import client.bean.Tank;
import client.client.TankClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Msg {
    private int msgType = Msg.TANK_DEAD_MESSAGE;
    private int tankId;
    private TankClient tc;

    public TankDeadMsg(int tankId){
        this.tankId = tankId;
    }

    public TankDeadMsg(TankClient tc){
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(tankId);
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
            if(tankId == this.tc.getMyTank().id){
                return;
            }
            for(Tank t : tc.getTanks()){
                if(t.id == tankId){
                    t.setLive(false);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
