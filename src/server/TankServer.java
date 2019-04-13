package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TankServer {

    public static int ID = 100;
    public static final int TCP_PORT = 8888;
    public static final int UDP_PORT = 6666;
    List<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        new TankServer().start();
    }

    public void start(){
        new Thread(new UDPThread()).start();
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

    public class Client{
        String IP;
        int UDP_PORT;

        public Client(String ipAddr, int UDP_PORT) {
            this.IP = ipAddr;
            this.UDP_PORT = UDP_PORT;
        }
    }
}
