import java.awt.*;
import java.util.List;

public class Missile {
    public static final int XSPEED = 10;
    public static final int YSPEED = 10;

    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static int ID = 10;

    public int id;

    TankClient tc;
    int tankId;

    int x, y;
    Dir dir = Dir.R;
    boolean live = true;
    boolean good;

    public Missile(int tankId, int x, int y, boolean good, Dir dir) {
        this.tankId = tankId;
        this.x = x;
        this.y = y;
        this.good = good;
        this.dir = dir;
        this.id = ID++;
    }

    public Missile(int tankId, int x, int y, boolean good, Dir dir, TankClient tc) {
        this(tankId, x, y, good, dir);
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!live) {
            tc.missiles.remove(this);
            return;
        }

        Color c = g.getColor();
        g.setColor(Color.BLACK);
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);

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

        if(x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
            live = false;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public boolean hitTank(Tank t) {
        if(this.live && t.isLive() && this.good != t.good && this.getRect().intersects(t.getRect())) {
            this.live = false;
            t.setLive(false);
            tc.explodes.add(new Explode(x, y, tc));
            return true;
        }
        return false;
    }

    public boolean hitTanks(List<Tank> tanks) {
        for(int i=0; i<tanks.size(); i++) {
            if(this.hitTank(tanks.get(i))) {
                return true;
            }
        }
        return false;
    }
}
