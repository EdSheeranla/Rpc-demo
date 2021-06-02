package sheeran.remoting.server.socket;

import org.apache.log4j.Logger;
import sheeran.remoting.server.RequestHandlerThread;
import sheeran.remoting.server.handler.RequestHandler;
import sheeran.provider.ServiceProvider;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer {
    //采用一个线程池来循环接收信息
    private final ExecutorService threadPool;

    private final static int CORE_POOL_SIZE = 5;
    private final static int MAXIMUM_POOL_SIZE = 50;
    private final static long KEEP_ALIVE_TIME = 60;
    private final static int BLOCKING_QUEUE_CAPACITY = 100;
    private final ServiceProvider serviceProvider;


    private static final Logger logger = Logger.getLogger(SocketServer.class);


    public SocketServer(ServiceProvider serviceProvider){
        this.serviceProvider = serviceProvider;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务器启动中");
            Socket socket;
            while((socket = serverSocket.accept())!=null){
                logger.info("消费者连接："+socket.getInetAddress()+" "+port);
                threadPool.execute(new RequestHandlerThread(socket,new RequestHandler(), serviceProvider));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void register (Object sheeran.service , int port){
//        try {
//            ServerSocket serverSocket = new ServerSocket(port);
//            logger.info("服务器启动中....");
//            Socket socket;
//            while((socket = serverSocket.accept())!=null){
//                logger.info("客户端连接, ip = "+socket.getInetAddress());
//                threadPool.execute(new WorkerThread(socket,sheeran.service));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
