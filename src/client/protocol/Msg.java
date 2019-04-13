package client.protocol;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
    public static final int TANK_NEW_MSG = 1;
    public static final int TANK_MOVE_MSG= 2;
    public static final int MISSILE_NEW_MESSAGE = 3;
    public static final int TANK_DEAD_MESSAGE = 4;
    public static final int MISSILE_DEAD_MESSAGE = 5;

    public void send(DatagramSocket ds, String IP, int UDP_Port);
    public void parse(DataInputStream dis);
}
