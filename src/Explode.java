import java.awt.*;

public class Explode {
    int x, y;
    private int[] diameters = {4, 7, 12, 18, 26, 32, 49, 30, 14, 6};
    private boolean live = true;

    private TankClient tc;
    int step = 0;

    public Explode(int x, int y, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!live) {
            tc.explodes.remove(this);
            return;
        }

        Color c = g.getColor();
        g.setColor(Color.ORANGE);
        g.fillOval(x, y, diameters[step], diameters[step]);
        g.setColor(c);

        step ++;
        if(step == diameters.length) {
            live = false;
        }
    }
}