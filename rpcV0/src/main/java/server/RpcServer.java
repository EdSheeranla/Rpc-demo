package server;

public interface RpcServer {
    void start();
    <T> void publishService(Object service,String serviceName);
}
