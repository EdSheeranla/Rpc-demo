package sheeran.registry;

import java.net.InetSocketAddress;

/*
 这里像zookeeper注册服务
 */
public interface ServiceRegistry {
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    InetSocketAddress lookupService(String serviceName);
}
