package client;

import client.netty.NettyClient;
import service.QueryService;
import service.dto.HelloObject;
import service.HelloService;
import service.dto.Student;

public class clientTest {
    public static void main(String[] args) {
        // TODO: 2021/5/26 测试多客户端连接
        // TODO: 2021/5/26 测试负载均衡 多服务注册
        NettyClient nettyClient = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        QueryService queryService = rpcClientProxy.getProxy(QueryService.class);
        String res = helloService.hello(object);
        Student student = queryService.Query(1);
        System.out.println(res);
        System.out.println(student.getId()+" : "+student.getName());
    }
}
