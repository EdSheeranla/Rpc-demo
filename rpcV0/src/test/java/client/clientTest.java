package client;

import server.HelloObject;
import server.HelloService;

public class clientTest {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13,"This is a message");
//        System.out.println((helloService.hello(object)).getClass());
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
