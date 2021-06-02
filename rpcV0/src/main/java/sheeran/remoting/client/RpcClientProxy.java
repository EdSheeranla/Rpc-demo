package sheeran.remoting.client;

import sheeran.remoting.client.dto.RpcRequest;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    private final Logger logger = Logger.getLogger(RpcClientProxy.class);


    private RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    //    通过反向代理实现服务接口
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info(method.getDeclaringClass().getName() + "   " + method.getName());
        RpcRequest rpcRequest = RpcRequest.build().interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .parameters(args)
                .paraType(method.getParameterTypes())
                .build();

        return rpcClient.sendRequest(rpcRequest);
    }
}
