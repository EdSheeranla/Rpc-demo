package sheeran.spring;

import sheeran.remoting.client.RpcClient;
import sheeran.remoting.client.RpcClientProxy;
import sheeran.remoting.client.netty.NettyClient;

public class ClientProxy {
    public static <T> T getServiceProxy(Class<T> serviceClass){

        RpcClient rpcClient = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        return rpcClientProxy.getProxy(serviceClass);
    }
}
