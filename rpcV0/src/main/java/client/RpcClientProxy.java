package client;

import org.apache.log4j.Logger;
import server.ResponseCode;
import server.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    private String host;

    private int port;

    private final Logger logger = Logger.getLogger(RpcClientProxy.class);

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info(method.getDeclaringClass().getName()+"   "+method.getName());
        RpcRequest rpcRequest = RpcRequest.build().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paraType(method.getParameterTypes())
                .build();
        RpcClient client = new RpcClient();
        Object response = client.sendRequest(rpcRequest,host,port);
        return ((RpcResponse) response).getData();
    }
}
