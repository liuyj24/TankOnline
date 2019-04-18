package client.bean;

import client.client.TankClient;
import client.protocol.MissileNewMsg;
import client.protocol.TankMoveMsg;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tank {
    private int id;

    public static final int XSPEED = 5;
    public static final int YSPEED = 5;

    private boolean good;
    private int x, y;
    private boolean live = true;
    private TankClient tc;
    private boolean bL, bU, bR, bD;
    private Dir dir = Dir.STOP;
    private Dir ptDir = Dir.D;
    private int blood;
    private BloodBar bb = new BloodBar();

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] imgs = null;
    private static Map<String, Image> map = new HashMap<>();
    static{
        imgs = new Image[]{//加载两方阵营的图片
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tD.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tL.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tLD.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tLU.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tR.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tRD.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tRU.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/tU.png")),

            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eD.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eL.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eLD.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eLU.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eR.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eRD.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eRU.png")),
            tk.getImage(Tank.class.getClassLoader().getResource("client/images/tank/eU.png")),
        };
        map.put("tD", imgs[0]);
        map.put("tL", imgs[1]);
        map.put("tLD", imgs[2]);
        map.put("tLU", imgs[3]);
        map.put("tR", imgs[4]);
        map.put("tRD", imgs[5]);
        map.put("tRU", imgs[6]);
        map.put("tU", imgs[7]);
        map.put("eD", imgs[8]);
        map.put("eL", imgs[9]);
        map.put("eLD", imgs[10]);
        map.put("eLU", imgs[11]);
        map.put("eR", imgs[12]);
        map.put("eRD", imgs[13]);
        map.put("eRU", imgs[14]);
        map.put("eU", imgs[15]);
    }

    public static final int WIDTH =  imgs[0].getWidth(null);
    public static final int HEIGHT = imgs[0].getHeight(null);

    public Tank(int x, int y, boolean good) {
        this.x = x;
        this.y = y;
        this.good = good;
        this.blood = 100;
    }

    public Tank(int x, int y, boolean good, Dir dir, TankClient tc) {
        this(x, y, good);
        this.dir = dir;
        this.tc = tc;
        this.blood = 100;
    }

    /**
     * 根据坦克阵营画出图片
     * @param g
     */
    public void draw(Graphics g) {
        if(!live) {
            if(!good) {
                tc.getTanks().remove(this);
            }
            return;
        }
        switch(ptDir) {
            case L:
                g.drawImage(good ? map.get("tL") : map.get("eL"), x, y, null);
                break;
            case LU:
                g.drawImage(good ? map.get("tLU") : map.get("eLU"), x, y, null);
                break;
            case U:
                g.drawImage(good ? map.get("tU") : map.get("eU"), x, y, null);
                break;
            case RU:
                g.drawImage(good ? map.get("tRU") : map.get("eRU"), x, y, null);
                break;
            case R:
                g.drawImage(good ? map.get("tR") : map.get("eR"), x, y, null);
                break;
            case RD:
                g.drawImage(good ? map.get("tRD") : map.get("eRD"), x, y, null);
                break;
            case D:
                g.drawImage(good ? map.get("tD") : map.get("eD"), x, y, null);
                break;
            case LD:
                g.drawImage(good ? map.get("tLD") : map.get("eLD"), x, y, null);
                break;
        }
        g.drawString("id:" + id, x, y - 20);
        bb.draw(g);//画出血条
        move();
    }

    /**
     * 根据坦克的方向进行移动
     */
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
    }

    /**
     * 监听键盘按下, 上下左右移动分别对应WSAD
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A:
                bL = true;
                break;
            case KeyEvent.VK_W:
                bU = true;
                break;
            case KeyEvent.VK_D:
                bR = true;
                break;
            case KeyEvent.VK_S:
                bD = true;
                break;
        }
        locateDirection();
    }

    /**
     * 根据4个方向的布尔值判断坦克的方向
     */
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
            TankMoveMsg msg = new TankMoveMsg(id, x, y, dir, ptDir);
            tc.getNc().send(msg);
        }
    }

    /**
     * 监听键盘释放
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_J://监听到J键按下则开火
                fire();
                break;
            case KeyEvent.VK_A:
                bL = false;
                break;
            case KeyEvent.VK_W:
                bU = false;
                break;
            case KeyEvent.VK_D:
                bR = false;
                break;
            case KeyEvent.VK_S:
                bD = false;
                break;
        }
        locateDirection();
    }

    private Missile fire() {//发出一颗炮弹的方法
        if(!live) return null;
        int x = this.x + 15 - 5;//确定子弹的坐标, 这里应该用子弹的常量计算, 待修正
        int y = this.y + 15 - 5;
        Missile m = new Missile(id, x, y, this.good, this.ptDir, this.tc);//产生一颗子弹
        tc.getMissiles().add(m);

        MissileNewMsg msg = new MissileNewMsg(m);
        tc.getNc().send(msg);
        return m;
    }

    /**
     * 血条
     */
    private class BloodBar {
        public void draw(Graphics g) {
            Color c = g.getColor();
            g.setColor(Color.BLACK);
            g.drawRect(x, y - 15, 30, 8);
            int w = (30 * blood) / 100 ;
            g.setColor(Color.RED);
            g.fillRect(x, y - 15, w, 8);
            g.setColor(c);
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
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

    public Dir getPtDir() {
        return ptDir;
    }

    public void setPtDir(Dir ptDir) {
        this.ptDir = ptDir;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }
}
