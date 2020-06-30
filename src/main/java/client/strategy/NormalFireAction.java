package client.strategy;

import client.bean.Missile;
import client.bean.Tank;
import client.protocol.MissileNewMsg;

public class NormalFireAction implements FireAction {
    @Override
    public void fireAction(Tank tank) {
        if(!tank.isLive()) return;
        int x = tank.getX() + 15 - 5;//确定子弹的坐标, 这里应该用子弹的常量计算, 待修正
        int y = tank.getY() + 15 - 5;
        Missile m = new Missile(tank.getId(), x, y, tank.isGood(), tank.getPtDir(), tank.getTc());//产生一颗子弹
        tank.getTc().getMissiles().add(m);

        MissileNewMsg msg = new MissileNewMsg(m);
        tank.getTc().getNc().send(msg);
    }
}
