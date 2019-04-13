package client.client;

import client.protocol.*;
import server.TankServer;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class NetClient {
    private String serverIP;
    private TankClient tc;
    private int UDP_PORT;
    private DatagramSocket ds = null;

    public void setUDP_PORT(int UDP_PORT) {
        this.UDP_PORT = UDP_PORT;
    }

    public NetClient(TankClient tc){
        this.tc = tc;
    }

    public void connect(String ip, int port){
        serverIP = ip;
        Socket s = null;
        try {
            ds = new DatagramSocket(UDP_PORT);
            s = new Socket(ip, port);
            System.out.println("connect successfully");
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(UDP_PORT);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            int id = dis.readInt();
            tc.getMyTank().id = id;
            tc.getMyTank().setGood((id & 1) == 0 ? true : false);
            System.out.println("get id" + tc.getMyTank().id);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TankNewMsg msg = new TankNewMsg(tc.getMyTank());
        send(msg);

        new Thread(new UDPThread()).start();
    }

    public void send(Msg msg){
        msg.send(ds, serverIP, TankServer.UDP_PORT);
    }

    public class UDPThread implements Runnable{


        byte[] buf = new byte[1024];

        @Override
        public void run() {
            while(null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                try{
                    ds.receive(dp);
                    parse(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket dp) {
            ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
            DataInputStream dis = new DataInputStream(bais);
            int msgType = 0;
            try {
                msgType = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Msg msg = null;
            switch (msgType){
                case Msg.TANK_NEW_MSG :
                    msg = new TankNewMsg(tc);
                    msg.parse(dis);
                    break;
                case  Msg.TANK_MOVE_MSG :
                    msg = new TankMoveMsg(tc);
                    msg.parse(dis);
                    break;
                case Msg.MISSILE_NEW_MESSAGE :
                    msg = new MissileNewMsg(tc);
                    msg.parse(dis);
                    break;
                case Msg.TANK_DEAD_MESSAGE :
                    msg = new TankDeadMsg(tc);
                    msg.parse(dis);
                    break;
                case Msg.MISSILE_DEAD_MESSAGE :
                    msg = new MissileDeadMsg(tc);
                    msg.parse(dis);
                    break;
            }
        }
    }
}
