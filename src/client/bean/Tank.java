package client.bean;

import client.client.TankClient;
import client.protocol.MissileNewMsg;
import client.protocol.TankMoveMsg;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
    private int id;

    public static final int XSPEED = 5;
    public static final int YSPEED = 5;
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;

    private boolean good;
    private int x, y;
    private boolean live = true;
    private TankClient tc;
    private boolean bL, bU, bR, bD;
    private Dir dir = Dir.STOP;
    private Dir ptDir = Dir.D;

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
                tc.getTanks().remove(this);
            }
            return;
        }

        Color c = g.getColor();
        if(good) g.setColor(Color.RED);
        else g.setColor(Color.BLUE);
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);
        g.drawString("id:" + id, x, y - 10);

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
    }

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

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_J:
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

    private Missile fire() {
        if(!live) return null;
        int x = this.x + WIDTH/2 - Missile.WIDTH/2;
        int y = this.y + HEIGHT/2 - Missile.HEIGHT/2;
        Missile m = new Missile(id, x, y, this.good, this.ptDir, this.tc);
        tc.getMissiles().add(m);

        MissileNewMsg msg = new MissileNewMsg(m);
        tc.getNc().send(msg);
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
}
