package client.bean;

import client.client.TankClient;
import client.event.TankHitEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Missile {
    public static final int XSPEED = 10;
    public static final int YSPEED = 10;
    private static int ID = 10;

    private int id;
    private int tankId;
    private int x, y;
    private Dir dir = Dir.R;
    private boolean live = true;
    private boolean good;

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] imgs = null;
    private static Map<String, Image> map = new HashMap<>();

    static {
        imgs = new Image[]{
                tk.getImage(Missile.class.getClassLoader().getResource("client/images/missile/m.png")),
                tk.getImage(Missile.class.getClassLoader().getResource("client/images/missile/n.png"))
        };
        map.put("n", imgs[0]);
        map.put("m", imgs[1]);
    }

    public static final int WIDTH = imgs[0].getWidth(null);
    public static final int HEIGHT = imgs[0].getHeight(null);

    public Missile(int tankId, int x, int y, boolean good, Dir dir) {
        this.tankId = tankId;
        this.x = x;
        this.y = y;
        this.good = good;
        this.dir = dir;
        this.id = ID++;
    }

    public Missile(int tankId, int x, int y, boolean good, Dir dir, int id) {
        this(tankId, x, y, good, dir);
        this.id = id;
    }

    public void draw(Graphics g) {
        if (!live) {
            TankClient.INSTANCE.getMissiles().remove(this);
            return;
        }
        g.drawImage(good ? map.get("n") : map.get("m"), x, y, null);
        move();
    }

    private void move() {//每画一次, 子弹的坐标移动一次
        switch (dir) {
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

        if (x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
            live = false;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
    }

    public boolean hitTank(Tank t) {//子弹击中坦克的方法
        if (this.live && t.isLive() && this.good != t.isGood() && this.getRect().intersects(t.getRect())) {
            this.live = false;//子弹死亡
            t.actionToTankHitEvent(new TankHitEvent(this));//告知观察的坦克被打中了
            return true;
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
