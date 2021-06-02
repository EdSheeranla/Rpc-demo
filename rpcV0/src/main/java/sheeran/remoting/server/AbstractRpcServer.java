package sheeran.remoting.server;

import sheeran.annotation.Service;
import sheeran.annotation.ServiceScan;
import sheeran.common.RpcError;
import sheeran.exceptions.RpcException;
import org.apache.log4j.Logger;
import sheeran.registry.ServiceRegistry;
import sheeran.provider.ServiceProvider;
import sheeran.common.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

// TODO: 2021/5/27 集成spring 使用spring进行注解
public  abstract class AbstractRpcServer {
    private final static Logger logger = Logger.getLogger(AbstractRpcServer.class);

    protected ServiceProvider serviceProvider;
    protected ServiceRegistry serviceRegistry;
    protected String host;
    protected int port;

    public  <T> void publishService(Object service, String serviceName){
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
    public void scanServices(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass = null;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少@ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)){
            basePackage = mainClassName.substring(0,mainClassName.lastIndexOf('.'));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz:classSet){
            if (clazz.isAnnotationPresent(Service.class)){
                String  serviceName = clazz.getAnnotation(Service.class).name();
                Object object;
                try {
                    try {
                        object = clazz.getDeclaredConstructor().newInstance();
                    } catch (InvocationTargetException | NoSuchMethodException e) {
                        logger.error("创建"+clazz+"出错");
                        e.printStackTrace();
                        continue;
                    }
                    if ("".equals(serviceName)){
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> oneInterface : interfaces){
                            publishService(object,oneInterface.getCanonicalName());
                        }
                    }else{
                        publishService(object,serviceName);
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
