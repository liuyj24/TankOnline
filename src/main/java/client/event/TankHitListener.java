package client.event;

/**
 * 坦克被击中事件监听者(由坦克实现)
 */
public interface TankHitListener {
    public void actionToTankHitEvent(TankHitEvent tankHitEvent);
}
