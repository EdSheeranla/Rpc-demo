package server;

import server.netty.NettyServer;
import service.HelloService;
import service.HelloServiceImpl;

public class serverTest {
    public static void main(String[] args) {

        HelloService helloService = new HelloServiceImpl();

        RpcServer rpcServer = new NettyServer("127.0.0.1",9000);
        rpcServer.publishService(helloService,helloService.getClass());
        rpcServer.start();

    }
}
