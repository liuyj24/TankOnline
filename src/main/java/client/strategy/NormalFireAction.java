package client.strategy;

import client.bean.Missile;
import client.bean.Tank;
import client.client.TankClient;
import client.msg.MissileNewMsg;

public class NormalFireAction implements FireAction {
    @Override
    public void fireAction(Tank tank) {
        if (!tank.isLive()) return;

        //确定子弹的坐标, 这里应该用子弹的常量计算, 待修正
        int x = tank.getX() + 15 - 5;
        int y = tank.getY() + 15 - 5;

        //产生一颗子弹
        Missile m = new Missile(tank.getId(), x, y, tank.isGood(), tank.getPtDir());
        TankClient.INSTANCE.getMissiles().add(m);

        MissileNewMsg msg = new MissileNewMsg(m);
        TankClient.INSTANCE.getNettyClient().send(msg);
    }
}
