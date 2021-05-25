package server.provider;
//服务注册表
public interface ServiceProvider {
    <T> void addServiceProvider(T service);
    Object getServiceProvider(String serviceName);


}
