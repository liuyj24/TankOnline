import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetClient {

    TankClient tc;
    public static int UPD_PORT_START = 5555;
    private int UDP_PORT;
    DatagramSocket ds = null;

    public NetClient(TankClient tc){
        UDP_PORT = UPD_PORT_START++;//注意在多线程下的同步问题.
        this.tc = tc;
        try {
            ds = new DatagramSocket(UDP_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void connect(String ip, int port){
        Socket s = null;
        try {
            s = new Socket(ip, port);
            System.out.println("connect successfully");
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(UDP_PORT);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            tc.myTank.id = dis.readInt();
            System.out.println("get id" + tc.myTank.id);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TankNewMsg msg = new TankNewMsg(tc.myTank);
        send(msg);

        new Thread(new UDPThread()).start();
    }

    public void send(Msg msg){
        msg.send(ds, "127.0.0.1", TankServer.UDP_PORT);
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
            }
        }
    }
}
