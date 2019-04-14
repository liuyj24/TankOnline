package server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TankServer {

    public static int ID = 100;
    public static final int TCP_PORT = 8888;
    public static final int UDP_PORT = 7777;
    public static final int TANK_DEAD_UDP_PORT = 6666;
    private List<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        new TankServer().start();
    }

    public void start(){
        new Thread(new UDPThread()).start();
        new Thread(new TankDeadUDPThread()).start();
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Socket s = null;
            try {
                s = ss.accept();
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int UDP_PORT = dis.readInt();
                Client client = new Client(s.getInetAddress().getHostAddress(), UDP_PORT);
                clients.add(client);

                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID++);
                dos.writeInt(TankServer.UDP_PORT);
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
                    System.out.println("recevice a packet~");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
