package client.client;

import client.bean.Dir;
import client.bean.Explode;
import client.bean.Missile;
import client.bean.Tank;
import client.protocol.MissileDeadMsg;
import client.protocol.TankDeadMsg;
import server.TankServer;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TankClient extends Frame {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    private Image offScreenImage = null;

    private Tank myTank = new Tank(50 + (int)(Math.random() * (GAME_WIDTH - 100)),
            50 + (int)(Math.random() * (GAME_HEIGHT - 100)), true, Dir.STOP, this);
    private NetClient nc = new NetClient(this);
    private ConDialog dialog = new ConDialog();
    private GameOverDialog gameOverDialog = new GameOverDialog();

    private List<Missile> missiles = new ArrayList<>();
    private List<Explode> explodes = new ArrayList<>();
    private List<Tank> tanks = new ArrayList<>();

    @Override
    public void paint(Graphics g) {
        g.drawString("missiles count:" + missiles.size(), 10, 50);
        g.drawString("explodes count:" + explodes.size(), 10, 70);
        g.drawString("tanks    count:" + tanks.size(), 10, 90);

        for(int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
            if(m.hitTank(myTank)){
                TankDeadMsg msg = new TankDeadMsg(myTank.getId());
                nc.send(msg);
                MissileDeadMsg mmsg = new MissileDeadMsg(m.getTankId(), m.getId());
                nc.send(mmsg);
                nc.sendTankDeadMsg();
                gameOverDialog.setVisible(true);
            }
            m.draw(g);
        }
        for(int i = 0; i < explodes.size(); i++) {
            Explode e = explodes.get(i);
            e.draw(g);
        }
        for(int i = 0; i < tanks.size(); i++) {
            Tank t = tanks.get(i);
            t.draw(g);
        }
        myTank.draw(g);
    }

    @Override
    public void update(Graphics g) {
        if(offScreenImage == null) {
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
        this.setTitle("TankWar");
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setBackground(Color.LIGHT_GRAY);

        this.addKeyListener(new KeyMonitor());

        this.setVisible(true);

        new Thread(new PaintThread()).start();

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        TankClient tc = new TankClient();
        tc.launchFrame();
    }


    class PaintThread implements Runnable {

        public void run() {
            while(true) {
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

    class ConDialog extends Dialog{
        Button b = new Button("connect to server");
        TextField tfIP = new TextField("127.0.0.1", 15);
        TextField tfPort = new TextField("" + TankServer.TCP_PORT, 4);
        TextField tfMyUDPPort = new TextField("5555", 4);

        public ConDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("IP:"));
            this.add(tfIP);
            this.add(new Label("Port:"));
            this.add(tfPort);
            this.add(new Label("My UDP Port:"));
            this.add(tfMyUDPPort);
            this.add(b);
            this.setLocation(400, 400);
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
                    int port = Integer.parseInt(tfPort.getText().trim());
                    int myUDPPort = Integer.parseInt(tfMyUDPPort.getText().trim());
                    nc.setUDP_PORT(myUDPPort);
                    nc.connect(IP, port);
                    setVisible(false);
                }
            });
        }
    }

    class GameOverDialog extends Dialog{
        Button b = new Button("exit");
        public GameOverDialog() {
            super(TankClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("Game Over~"));
            this.add(b);
            this.setLocation(400, 400);
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

    public List<Missile> getMissiles() {
        return missiles;
    }

    public void setMissiles(List<Missile> missiles) {
        this.missiles = missiles;
    }

    public List<Explode> getExplodes() {
        return explodes;
    }

    public void setExplodes(List<Explode> explodes) {
        this.explodes = explodes;
    }

    public List<Tank> getTanks() {
        return tanks;
    }

    public void setTanks(List<Tank> tanks) {
        this.tanks = tanks;
    }

    public Tank getMyTank() {
        return myTank;
    }

    public void setMyTank(Tank myTank) {
        this.myTank = myTank;
    }

    public NetClient getNc() {
        return nc;
    }

    public void setNc(NetClient nc) {
        this.nc = nc;
    }
}