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

/**
 * 旧坦克向新坦克发送消息的协议
 */
public class TankAlreadyExistMsg implements Msg {
    private int msgType = Msg.TANK_ALREADY_EXIST_MSG;
    private Tank tank;
    private TankClient tc;

    public TankAlreadyExistMsg(Tank tank){
        this.tank = tank;
    }

    public TankAlreadyExistMsg(TankClient tc){
        this.tc = tc;
    }

    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(88);
        DataOutputStream dos = new DataOutputStream(baos);
        try{
            dos.writeInt(msgType);
            dos.writeInt(tank.getId());
            dos.writeInt(tank.getX());
            dos.writeInt(tank.getY());
            dos.writeInt(tank.getDir().ordinal());
            dos.writeBoolean(tank.isGood());
            dos.writeUTF(tank.getName());
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
            if(id == tc.getMyTank().getId()){
                return;
            }
            boolean exist = false;
            for(Tank t : tc.getTanks()){
                if(id == t.getId()){
                    exist = true;
                    break;
                }
            }
            if(!exist){
                int x = dis.readInt();
                int y = dis.readInt();
                Dir dir = Dir.values()[dis.readInt()];
                boolean good = dis.readBoolean();
                String name = dis.readUTF();
                Tank existTank = new Tank(name, x, y, good, dir, tc);
                existTank.setId(id);
                tc.getTanks().add(existTank);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
