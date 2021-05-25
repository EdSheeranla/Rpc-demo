package server.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import server.registry.RegistryConstant;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CuratorUtil {
    private final static Logger logger = Logger.getLogger(CuratorUtil.class);
    public final static String ZK_ROOT_NODE ="/RPC_h";
    private final static String ZK_SERVER_URL = "127.0.0.1:2181";
    private static CuratorFramework zkClient;
    private final static int BASE_SLEEP_TIME  = 1000;
    private final static int MAX_RETRY  = 3;
    private final static Set<String> EXISTED_NODE = ConcurrentHashMap.newKeySet();
    private final static Map<String,List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private CuratorUtil(){

    }

    /**
     * 获取Zookeeper客户端实例
     * @return
     */
    public static CuratorFramework getZkClient(){
        if (zkClient!=null && zkClient.getState() == CuratorFrameworkState.STARTED) return zkClient;

        zkClient = CuratorFrameworkFactory.builder()
                .connectString(ZK_SERVER_URL)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIME,MAX_RETRY))
                .build();
        zkClient.start();
        try {
            if(!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Time our waiting to connect ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    /**
     * 创建持久节点
     * @param zkClient
     * @param path
     */
    public static  void createPersistentNode(CuratorFramework zkClient,String path){
        try {
            if (EXISTED_NODE.contains(path) || zkClient.checkExists().forPath(path)!=null){
                logger.info("节点已创建："+path);
            }else{
                zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                logger.info("创建节点成功: "+ path);
            }
            EXISTED_NODE.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取注册服务对应的地址
     * @param zkClient
     * @param serviceName
     * @return
     */
    public static List<String> getChildren(CuratorFramework zkClient,String serviceName){
        if (SERVICE_ADDRESS_MAP.containsKey(serviceName)){
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        String servicePath = ZK_ROOT_NODE+"/"+serviceName;
        List<String> res = null;
        try {
            res = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName,res);
            registerWatcher(serviceName,zkClient);
        } catch (Exception e) {
            logger.error("获取服务节点出错: "+servicePath);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 注册监听器 注册服务发生变化会通知客户端
     * @param serviceName
     * @param zkClient
     */
    public static void registerWatcher(String serviceName,CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_ROOT_NODE+"/"+serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient,servicePath,true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework,pathChildrenCacheEvent) ->{
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName,serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }


}
