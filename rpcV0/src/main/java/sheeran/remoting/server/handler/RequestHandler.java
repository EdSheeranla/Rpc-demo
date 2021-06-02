package sheeran.remoting.server.handler;

import sheeran.remoting.client.dto.RpcRequest;
import org.apache.log4j.Logger;
import sheeran.remoting.server.dto.ResponseCode;
import sheeran.remoting.server.dto.RpcResponse;
import sheeran.provider.ServiceProvider;
import sheeran.provider.ServiceProviderImpl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private final Logger logger = Logger.getLogger(RequestHandler.class);
    private final ServiceProvider serviceProvider;
    public RequestHandler(){
        this.serviceProvider = new ServiceProviderImpl();
    }

    public Object handle (RpcRequest rpcRequest)  {
        Object result = null;
        try {
            String serviceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getServiceProvider(serviceName);
            result = invokeTargetMethod(rpcRequest, service);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException{
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParaTypes());
        } catch (NoSuchMethodException e) {
            logger.error("没有找到指定方法");
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
