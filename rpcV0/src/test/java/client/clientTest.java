package client;

import client.netty.NettyClient;
import service.HelloObject;
import service.HelloService;

public class clientTest {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
