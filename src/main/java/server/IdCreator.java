package server;

/**
 * id分派器
 * <p>
 * 每来一个客户端分配一个id
 */
public class IdCreator {

    public static final IdCreator INSTANCE = new IdCreator();

    private static int id = 100;

    public static int getId() {
        return id++;
    }
}
