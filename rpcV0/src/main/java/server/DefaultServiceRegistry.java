package server;

import common.RpcError;
import exceptions.RpcException;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {
    private final static Logger logger = Logger.getLogger(DefaultServiceRegistry.class);
    private final static Map<String, Object> serviceMap = new ConcurrentHashMap<String, Object>();
    private final static Set<String> registeredService = ConcurrentHashMap.newKeySet();

    public <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
            logger.info("像接口: " + i + "注册服务:" + serviceName);
        }

    }

    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
