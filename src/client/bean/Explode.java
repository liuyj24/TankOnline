package client.bean;

import client.client.TankClient;

import java.awt.*;

public class Explode {
    private int x, y;
    private int[] diameters = {4, 7, 12, 18, 26, 32, 49, 30, 14, 6};
    private boolean live = true;
    private TankClient tc;

    private int step = 0;
    private static boolean init = false;
    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] images = {
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode1.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode2.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode3.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode4.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode5.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode6.png")),
        tk.getImage(Explode.class.getClassLoader().getResource("client/images/explode/explode7.png"))
    };

    public Explode(int x, int y, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!init){
            for(int i = 0; i < images.length; i++){
                g.drawImage(images[i], -100, -100, null);
            }
            init = true;
        }
        if(!live) {
            tc.getExplodes().remove(this);
            return;
        }
        if(step == images.length){
            live = false;
            step = 0;
            return;
        }
        g.drawImage(images[step++], x, y, null);
    }
}