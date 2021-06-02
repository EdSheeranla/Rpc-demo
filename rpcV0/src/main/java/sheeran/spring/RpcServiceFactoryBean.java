package sheeran.spring;

import org.springframework.beans.factory.FactoryBean;

public class RpcServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> rpcInterface;

    public RpcServiceFactoryBean() {
    }

    public RpcServiceFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public T getObject() throws Exception {
        if (rpcInterface == null){
            throw new IllegalStateException("");
        }
        return ClientProxy.getServiceProxy(rpcInterface);
    }

    @Override
    public Class<?> getObjectType() {

        return rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
