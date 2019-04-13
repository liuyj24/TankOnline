package client.bean;

import client.client.TankClient;
import java.awt.*;
import java.util.List;

public class Missile {
    public static final int XSPEED = 10;
    public static final int YSPEED = 10;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    private static int ID = 10;

    private int id;
    private TankClient tc;
    private int tankId;
    private int x, y;
    private Dir dir = Dir.R;
    private boolean live = true;
    private boolean good;

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
            tc.getMissiles().remove(this);
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
        if(this.live && t.isLive() && this.good != t.isGood() && this.getRect().intersects(t.getRect())) {
            this.live = false;
            t.setLive(false);
            tc.getExplodes().add(new Explode(x, y, tc));
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
    }
}
