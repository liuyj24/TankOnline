package server;

import client.client.TankClient;

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

import static client.client.TankClient.GAME_HEIGHT;
import static client.client.TankClient.GAME_WIDTH;

/**
 * 服务器端
 */
public class TankServer extends Frame {

    public static int ID = 100;//id号的初始序列
    public static final int TCP_PORT = 55555;//TCP端口号
    private Image offScreenImage = null;//服务器画布
    private static final int SERVER_HEIGHT = 500;
    private static final int SERVER_WIDTH = 300;


    public void start() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_PORT);//在TCP欢迎套接字上监听客户端连接
            System.out.println("TankServer has started...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket s = null;
            try {
                s = ss.accept();//给客户但分配专属TCP套接字
                System.out.println("A client has connected...");

                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                dos.writeInt(ID++);//向客户端分配id号

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


//    /************** 服务器可视化 **************/
//    @Override
//    public void paint(Graphics g) {
//        g.drawString("TankClient :", 30, 50);
//        int y = 80;
//        for(int i = 0; i < clients.size(); i++){//显示出每个客户端的信息
//            Client c = clients.get(i);
//            g.drawString("id : " + c.id + " - IP : " + c.IP, 30, y);
//            y += 30;
//        }
//    }

    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(SERVER_WIDTH, SERVER_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.yellow);
        gOffScreen.fillRect(0, 0, SERVER_WIDTH, SERVER_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    public void launchFrame() {
        this.setLocation(200, 100);
        this.setSize(SERVER_WIDTH, SERVER_HEIGHT);
        this.setTitle("TankServer");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setBackground(Color.yellow);
        this.setVisible(true);
        new Thread(new PaintThread()).start();

    }

    /**
     * 重画线程
     */
    class PaintThread implements Runnable {
        public void run() {
            while (true) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
