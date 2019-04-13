import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
    int id;

    public static final int XSPEED = 5;
    public static final int YSPEED = 5;

    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;

    boolean good;
    int x, y;

    private static Random r = new Random();

    private boolean live = true;

    private int step = r.nextInt(12) + 3;

    TankClient tc;

    boolean bL, bU, bR, bD;

    Dir dir = Dir.STOP;
    Dir ptDir = Dir.D;

    public Tank(int x, int y, boolean good) {
        this.x = x;
        this.y = y;
        this.good = good;
    }

    public Tank(int x, int y, boolean good, Dir dir, TankClient tc) {
        this(x, y, good);
        this.dir = dir;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!live) {
            if(!good) {
                tc.tanks.remove(this);
            }
            return;
        }

        Color c = g.getColor();
        if(good) g.setColor(Color.RED);
        else g.setColor(Color.BLUE);
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);

        switch(ptDir) {
            case L:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x, y + HEIGHT/2);
                break;
            case LU:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x, y);
                break;
            case U:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH/2, y);
                break;
            case RU:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH, y);
                break;
            case R:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH, y + HEIGHT/2);
                break;
            case RD:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH, y + HEIGHT);
                break;
            case D:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x + WIDTH/2, y + HEIGHT);
                break;
            case LD:
                g.drawLine(x + WIDTH/2, y + HEIGHT/2, x, y + HEIGHT);
                break;
        }

        move();
    }

    private void move() {
        switch(dir) {
            case L:
                x -= XSPEED;
                break;
            case LU:
                x -= XSPEED;
                y -= YSPEED;
                break;
            case U:
                y -= YSPEED;
                break;
            case RU:
                x += XSPEED;
                y -= YSPEED;
                break;
            case R:
                x += XSPEED;
                break;
            case RD:
                x += XSPEED;
                y += YSPEED;
                break;
            case D:
                y += YSPEED;
                break;
            case LD:
                x -= XSPEED;
                y += YSPEED;
                break;
            case STOP:
                break;
        }

        if(dir != Dir.STOP) {
            ptDir = dir;
        }

        if(x < 0) x = 0;
        if(y < 30) y = 30;
        if(x + WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - WIDTH;
        if(y + HEIGHT > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - HEIGHT;

        if(!good) {
            if(step == 0) {
                step = r.nextInt(12) + 3;
                Dir[] dirs = Dir.values();
                dir = dirs[r.nextInt(dirs.length)];
            }
            step --;
            if(r.nextInt(40) > 38) this.fire();
        }

    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                bL = true;
                break;
            case KeyEvent.VK_UP:
                bU = true;
                break;
            case KeyEvent.VK_RIGHT:
                bR = true;
                break;
            case KeyEvent.VK_DOWN:
                bD = true;
                break;
        }
        locateDirection();
    }

    private void locateDirection() {
        Dir oldDir = this.dir;
        if(bL && !bU && !bR && !bD) dir = Dir.L;
        else if(bL && bU && !bR && !bD) dir = Dir.LU;
        else if(!bL && bU && !bR && !bD) dir = Dir.U;
        else if(!bL && bU && bR && !bD) dir = Dir.RU;
        else if(!bL && !bU && bR && !bD) dir = Dir.R;
        else if(!bL && !bU && bR && bD) dir = Dir.RD;
        else if(!bL && !bU && !bR && bD) dir = Dir.D;
        else if(bL && !bU && !bR && bD) dir = Dir.LD;
        else if(!bL && !bU && !bR && !bD) dir = Dir.STOP;

        if(dir != oldDir){
            TankMoveMsg msg = new TankMoveMsg(tc);
            tc.nc.send(msg);
        }

    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_CONTROL:
                fire();
                break;
            case KeyEvent.VK_LEFT:
                bL = false;
                break;
            case KeyEvent.VK_UP:
                bU = false;
                break;
            case KeyEvent.VK_RIGHT:
                bR = false;
                break;
            case KeyEvent.VK_DOWN:
                bD = false;
                break;
        }
        locateDirection();
    }

    private Missile fire() {
        int x = this.x + WIDTH/2 - Missile.WIDTH/2;
        int y = this.y + HEIGHT/2 - Missile.HEIGHT/2;
        Missile m = new Missile(x, y, this.good, this.ptDir, this.tc);
        tc.missiles.add(m);
        return m;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
