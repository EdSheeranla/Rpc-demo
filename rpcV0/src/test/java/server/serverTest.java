package server;

import annotation.ServiceScan;
import client.netty.NettyClient;
import loadBalance.RoundRobinLoadBalancer;
import server.netty.NettyServer;
import service.HelloService;
import service.QueryService;
import service.impl.HelloServiceImpl;
import service.impl.QueryServiceImpl;

@ServiceScan(value = "service")
public class serverTest {

public static void main(String[] args) {
    NettyServer rpcServer = new NettyServer("127.0.0.1",9000,new RoundRobinLoadBalancer());
    rpcServer.start();
}

}
