package client.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

public class NetClient {
    /**
     * 与服务器进行TCP连接
     */
    public void connect() {
        Socket s = null;
        try {
            s = new Socket("127.0.0.1", 55555);//创建TCP套接字

            DataInputStream dis = new DataInputStream(s.getInputStream());
            int id = dis.readInt();//获得自己的id号

            TankClient.INSTANCE.getMyTank().setId(id);//设置坦克的id号
            TankClient.INSTANCE.getMyTank().setGood((id & 1) == 0 ? true : false);//根据坦克的id号分配阵营

            System.out.println("get id successfully...");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
