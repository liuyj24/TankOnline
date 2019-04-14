package server;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器端
 */
public class TankServer {

    public static int ID = 100;//id号的初始序列
    public static final int TCP_PORT = 8888;//TCP端口号
    public static final int UDP_PORT = 7777;//转发客户端数据的UDP端口号
    public static final int TANK_DEAD_UDP_PORT = 6666;//接收客户端坦克死亡的端口号
    private List<Client> clients = new ArrayList<>();//客户端集合

    public static void main(String[] args) {
        new TankServer().start();
    }

    public void start(){
        new Thread(new UDPThread()).start();
        new Thread(new TankDeadUDPThread()).start();
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_PORT);//在TCP欢迎套接字上监听客户端连接
            System.out.println("TankServer has started...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Socket s = null;
            try {
                s = ss.accept();//给客户但分配专属TCP套接字
                System.out.println("A client has connected...");
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int UDP_PORT = dis.readInt();//记录客户端UDP端口
                Client client = new Client(s.getInetAddress().getHostAddress(), UDP_PORT);
                clients.add(client);//添加进客户端容器

                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID++);//向客户端分配id号
                dos.writeInt(TankServer.UDP_PORT);//告诉客户端自己的UDP端口号
                dos.writeInt(TankServer.TANK_DEAD_UDP_PORT);
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UDPThread implements Runnable{

        byte[] buf = new byte[1024];

        @Override
        public void run() {
            DatagramSocket ds = null;
            try{
                ds = new DatagramSocket(UDP_PORT);
            }catch (SocketException e) {
                e.printStackTrace();
            }

            while (null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                try {
                    ds.receive(dp);
                    for (Client c : clients){
                        dp.setSocketAddress(new InetSocketAddress(c.IP, c.UDP_PORT));
                        ds.send(dp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 监听坦克死亡的UDP线程
     */
    private class TankDeadUDPThread implements Runnable{
        byte[] buf = new byte[300];
        @Override
        public void run() {
            DatagramSocket ds = null;
            try{
                ds = new DatagramSocket(TANK_DEAD_UDP_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while(null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                ByteArrayInputStream bais = null;
                DataInputStream dis = null;
                try{
                    ds.receive(dp);
                    bais = new ByteArrayInputStream(buf, 0, dp.getLength());
                    dis = new DataInputStream(bais);
                    int deadTankUDPPort = dis.readInt();
                    for(int i = 0; i < clients.size(); i++){
                        Client c = clients.get(i);
                        if(c.UDP_PORT == deadTankUDPPort){
                            clients.remove(c);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (null != dis){
                        try {
                            dis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(null != bais){
                        try {
                            bais.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public class Client{
        String IP;
        int UDP_PORT;

        public Client(String ipAddr, int UDP_PORT) {
            this.IP = ipAddr;
            this.UDP_PORT = UDP_PORT;
        }
    }
}
