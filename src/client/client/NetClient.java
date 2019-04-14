package client.client;

import client.protocol.*;
import server.TankServer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetClient {
    private TankClient tc;
    private int UDP_PORT;
    private String serverIP;
    private int serverUDPPort;
    private int TANK_DEAD_UDP_PORT;
    private DatagramSocket ds = null;

    public void setUDP_PORT(int UDP_PORT) {
        this.UDP_PORT = UDP_PORT;
    }

    public NetClient(TankClient tc){
        this.tc = tc;
    }

    /**
     * @param ip server IP
     * @param port  server TCP port
     */
    public void connect(String ip, int port){
        serverIP = ip;
        Socket s = null;
        try {
            ds = new DatagramSocket(UDP_PORT);
            s = new Socket(ip, port);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(UDP_PORT);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            int id = dis.readInt();
            this.serverUDPPort = dis.readInt();
            this.TANK_DEAD_UDP_PORT = dis.readInt();
            tc.getMyTank().setId(id);
            tc.getMyTank().setGood((id & 1) == 0 ? true : false);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Thread(new UDPThread()).start();

        TankNewMsg msg = new TankNewMsg(tc.getMyTank());
        send(msg);
    }

    public void send(Msg msg){
        msg.send(ds, serverIP, serverUDPPort);
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
                case Msg.TANK_ALREADY_EXIST :
                    msg = new TankAlreadyExistMsg(tc);
                    msg.parse(dis);
            }
        }
    }

    public void sendTankDeadMsg(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream(88);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(UDP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != dos){
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != baos){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        byte[] buf = baos.toByteArray();
        try{
            DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(serverIP, TANK_DEAD_UDP_PORT));
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
