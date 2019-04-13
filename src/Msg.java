import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
    public static final int TANK_NEW_MSG = 1;
    public static final int TANK_MOVE_MSG= 2;

    public void send(DatagramSocket ds, String IP, int UDP_Port);
    public void parse(DataInputStream dis);
}
