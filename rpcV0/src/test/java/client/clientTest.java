package client;

import server.HelloObject;
import server.HelloService;

public class clientTest {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
