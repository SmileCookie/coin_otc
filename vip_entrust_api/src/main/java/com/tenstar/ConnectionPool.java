package com.tenstar;

/**
 * TCP开发 连接池
 * */
import java.net.Socket;
import java.util.Hashtable;
public class ConnectionPool {
    public static  int CONNECTION_POOL_SIZE = 10;
    public static  String API_SERVER_HOST = "127.0.0.1";
    public static  int API_SERVER_PORT = 800; 
    private static ConnectionPool self = null;// ConnectionPool的唯一实例
    private Hashtable socketPool = null;// 连接池
    private boolean[] socketStatusArray = null;// 连接的状态（true-被占用；false-空闲）
    public static synchronized void init() {
        self = new ConnectionPool();
        self.socketPool = new Hashtable();
        self.socketStatusArray = new boolean[CONNECTION_POOL_SIZE];
        // 初始化连接池
        self.buildConnectionPool();
    }
    public static synchronized void reset() {
        self = null;
        init();
    }
    public static Socket getConnection() {
        if (self == null)
            init();
        int i = 0;
        for (i = 0; i < CONNECTION_POOL_SIZE; i++) {
            if (!self.socketStatusArray[i]) {
                self.socketStatusArray[i] = true;
                break;
            }
        }
        if (i <= CONNECTION_POOL_SIZE)
            return (Socket) self.socketPool.get(new Integer(i));
        else {
            System.out.println("从连接池中获取与邮局的连接失败，已经没有空闲连接！");
            throw new RuntimeException("No enough pooled connections.");
        }
    }
    public static void releaseConnection(Socket socket) {
        if (self == null)
            init();
        for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
            if (((Socket) self.socketPool.get(new Integer(i))) == socket) {
                self.socketStatusArray[i] = false;
                break;
            }
        }
    }
    public static Socket rebuildConnection(Socket socket) {
        if (self == null)
            init();
        Socket newSocket = null;
        for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
            try {
                if (((Socket) self.socketPool.get(new Integer(i))) == socket) {
                    System.out.println("重建连接池中的第" + i + "个连接.");
                    newSocket = new Socket(API_SERVER_HOST, API_SERVER_PORT);
                    self.socketPool.put(new Integer(i), newSocket);
                    self.socketStatusArray[i] = true;
                    break;
                }
            } catch (Exception e) {
                System.out.println("重建连接失败！");
                throw new RuntimeException(e);
            }
        }
        return newSocket;
    }
    public synchronized static void buildConnectionPool() {
        if (self == null)
            init();
        System.out.println("准备建立连接池.");
        Socket socket = null;
        try {
            for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
                socket = new Socket(API_SERVER_HOST, API_SERVER_PORT);
                self.socketPool.put(new Integer(i), socket);
                self.socketStatusArray[i] = false;
            }
        } catch (Exception e) {
            System.out.println("与邮局的连接池建立失败！");
            throw new RuntimeException(e);
        }
    }
    public synchronized static void releaseAllConnection() {
        if (self == null)
            init();
        // 关闭所有连接
        Socket socket = null;
        for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
            socket = (Socket) self.socketPool.get(new Integer(i));
        
            try {
              socket.close();
            } catch (Exception e) {
            }
        }
    }
}
