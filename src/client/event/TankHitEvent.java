package client.event;

import client.bean.Missile;

public class TankHitEvent {
    private Missile source;

    public TankHitEvent(Missile source){
        this.source = source;
    }

    public Missile getSource() {
        return source;
    }

    public void setSource(Missile source) {
        this.source = source;
    }
}
