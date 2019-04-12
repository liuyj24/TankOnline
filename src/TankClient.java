import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    Tank myTank = new Tank(50, 50, true, Dir.STOP, this);

    List<Missile> missiles = new ArrayList<Missile>();
    List<Explode> explodes = new ArrayList<Explode>();
    List<Tank> tanks = new ArrayList<Tank>();

    Image offScreenImage = null;

    @Override
    public void paint(Graphics g) {
        g.drawString("missiles count:" + missiles.size(), 10, 50);
        g.drawString("explodes count:" + explodes.size(), 10, 70);
        g.drawString("tanks    count:" + tanks.size(), 10, 90);

        for(int i=0; i<missiles.size(); i++) {
            Missile m = missiles.get(i);
            m.hitTanks(tanks);
            m.draw(g);
        }

        for(int i=0; i<explodes.size(); i++) {
            Explode e = explodes.get(i);
            e.draw(g);
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
        gOffScreen.setColor(Color.GREEN);
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
        this.setBackground(Color.GREEN);

        this.addKeyListener(new KeyMonitor());

        this.setVisible(true);

        new Thread(new PaintThread()).start();
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
}
