package client.client;

import client.bean.Dir;
import client.bean.Explode;
import client.bean.Missile;
import client.bean.Tank;
import client.msg.GetIdMsg;
import client.msg.MissileDeadMsg;
import lombok.Data;
import lombok.Getter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class TankClient extends Frame {
    public static final TankClient INSTANCE = new TankClient();

    private TankClient() {
    }

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private Image offScreenImage = null;

    //客户端的坦克
    private Tank myTank;

    //Netty客户端
    private NettyClient nettyClient = NettyClient.INSTANCE;

    //连接服务器对话框
    private ConDialog dialog = new ConDialog();

    //游戏结束对话框
    private GameOverDialog gameOverDialog = new GameOverDialog();

    //UDP分配失败对话框
    private UDPPortWrongDialog udpPortWrongDialog = new UDPPortWrongDialog();

    //服务器未启动对话框
    private ServerNotStartDialog serverNotStartDialog = new ServerNotStartDialog();

    //存储游戏中的子弹集合
    private List<Missile> missiles = new ArrayList<>();

    //爆炸集合
    private List<Explode> explodes = new ArrayList<>();

    //坦克集合
    private List<Tank> tanks = new ArrayList<>();

    public static void main(String[] args) {
        //启动客户端界面
        INSTANCE.launchFrame();

        NetClient netClient = new NetClient();
        netClient.connect();

        //连接Netty服务器
        NettyClient.INSTANCE.connect();

    }

    @Override
    public void paint(Graphics g) {
        g.drawString("missiles count:" + missiles.size(), 10, 50);
        g.drawString("explodes count:" + explodes.size(), 10, 70);
        g.drawString("tanks    count:" + tanks.size(), 10, 90);

        for (int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
            if (m.hitTank(myTank)) {
//                TankDeadMsg msg = new TankDeadMsg(myTank.getId());
//                nc.send(msg);
                MissileDeadMsg mmsg = new MissileDeadMsg(m.getTankId(), m.getId());
                this.nettyClient.send(mmsg);
//                nc.sendClientDisconnectMsg();
//                gameOverDialog.setVisible(true);
            }
            m.draw(g);
        }
        for (int i = 0; i < explodes.size(); i++) {
            Explode e = explodes.get(i);
            e.draw(g);
        }
        for (int i = 0; i < tanks.size(); i++) {
            Tank t = tanks.get(i);
            t.draw(g);
        }
        if (null != myTank) {
            myTank.draw(g);
        }
    }

    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(800, 600);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.LIGHT_GRAY);
        gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    public void launchFrame() {
        this.setLocation(400, 300);

        this.setSize(GAME_WIDTH, GAME_HEIGHT);

        this.setTitle("TankClient");

        //监听关闭窗口事件
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //todo 关闭窗口的逻辑
                System.exit(0);
            }
        });

        //设置窗口大小不可改变
        this.setResizable(false);

        //设置窗口的背景颜色
        this.setBackground(Color.LIGHT_GRAY);

        //添加键盘监听事件
        this.addKeyListener(new KeyMonitor());

        //设置窗口可见
        this.setVisible(true);

        new Thread(new PaintThread()).start();

        this.dialog.setVisible(true);

        //添加排行榜按钮
        this.addRankButton();
    }

    /**
     * 创建排行榜查看按钮
     */
    public void addRankButton(){

        Button rankButton = new Button();
        rankButton.setLabel("show rank");
        rankButton.setBounds(GAME_WIDTH - 100, 50, 80,30);

        //把按钮添加到窗口中
        this.add(rankButton);

        //设置焦点不聚集在按键上
        rankButton.setFocusable(false);

        rankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //
            }
        });
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

    class KeyMonitor extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            myTank.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            myTank.keyPressed(e);
        }
    }

    /**
     * 游戏开始前连接到服务器的对话框
     */
    class ConDialog extends Dialog {
        Button b = new Button("connect to server");
        TextField tfIP = new TextField("127.0.0.1", 15);//服务器的IP地址
        TextField tfTankName = new TextField("myTank", 8);

        public ConDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("server IP:"));
            this.add(tfIP);
            this.add(new Label("tank name:"));
            this.add(tfTankName);
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String IP = tfIP.getText().trim();
                    String tankName = tfTankName.getText().trim();
                    myTank = new Tank(tankName, 50 + (int) (Math.random() * (GAME_WIDTH - 100)),
                            50 + (int) (Math.random() * (GAME_HEIGHT - 100)), true, Dir.STOP);

                    setVisible(false);
                }
            });
        }
    }

    /**
     * 坦克死亡后退出的对话框
     */
    class GameOverDialog extends Dialog {
        Button b = new Button("exit");

        public GameOverDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("Game Over~"));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    /**
     * UDP端口分配失败后的对话框
     */
    class UDPPortWrongDialog extends Dialog {
        Button b = new Button("ok");

        public UDPPortWrongDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("something wrong, please connect again"));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    /**
     * 连接服务器失败后的对话框
     */
    class ServerNotStartDialog extends Dialog {
        Button b = new Button("ok");

        public ServerNotStartDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("The server has not been opened yet..."));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    public void gameOver() {
        this.gameOverDialog.setVisible(true);
    }
}