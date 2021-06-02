package sheeran.registry;

import sheeran.common.RpcError;
import sheeran.exceptions.RpcException;
import sheeran.loadBalance.LoadBalancer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceRegistry implements ServiceRegistry {

    private static final Logger logger = Logger.getLogger(ZKServiceRegistry.class);

    private final LoadBalancer loadBalancer;

    public ZKServiceRegistry(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            CuratorFramework zkClient = CuratorUtil.getZkClient();
            String serviceNodePath = CuratorUtil.ZK_ROOT_NODE + "/" + serviceName + inetSocketAddress.toString();
            CuratorUtil.createPersistentNode(zkClient, serviceNodePath);
            logger.info("注册服务成功: " + serviceName);
        } catch (Exception e) {
            logger.error("注册服务失败", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        CuratorFramework zkClient = CuratorUtil.getZkClient();

        List<String> children = CuratorUtil.getChildren(zkClient, serviceName);

        if (children == null || children.isEmpty()) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        String targetUrl = loadBalancer.select(children);
        String[] sockAddressArray = targetUrl.split(":");
        String host = sockAddressArray[0];
        int port = Integer.parseInt(sockAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
