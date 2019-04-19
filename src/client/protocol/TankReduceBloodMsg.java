package client.protocol;

import client.bean.Explode;
import client.bean.Missile;
import client.bean.Tank;
import client.client.TankClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankReduceBloodMsg implements Msg {
    private int msgType = Msg.TANK_REDUCE_BLOOD_MSG;
    private int tankId;
    private Missile m;
    private TankClient tc;

    public TankReduceBloodMsg(int tankId, Missile m){
        this.tankId = tankId;
        this.m = m;
    }

    public TankReduceBloodMsg(TankClient tc){
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(50);
        DataOutputStream dos = new DataOutputStream(baos);
        try{
            dos.writeInt(msgType);
            dos.writeInt(tankId);//发送扣血坦克的id
            dos.writeInt(m.getX() - 20);
            dos.writeInt(m.getY() - 20);
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
        try {
            int id = dis.readInt();
            if(id == tc.getMyTank().getId()){
                return;
            }
            int bX = dis.readInt();
            int bY = dis.readInt();
            this.tc.getExplodes().add(new Explode(bX, bY, this.tc));
            for(Tank t : tc.getTanks()){
                if(t.getId() == id){
                    t.setBlood(t.getBlood() - 20);//找到扣血的坦克, 减少20滴血
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
