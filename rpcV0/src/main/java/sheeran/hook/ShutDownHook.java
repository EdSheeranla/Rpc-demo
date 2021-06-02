package sheeran.hook;

import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;
import sheeran.registry.CuratorUtil;

/**
 * 钩子函数 关闭系统的时候自动注销服务
 */
public class ShutDownHook {
    private static final Logger logger = Logger.getLogger(ShutDownHook.class);

    private static final ShutDownHook shutdownHook = new ShutDownHook();

    public static ShutDownHook getInstance() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorFramework zkClient = CuratorUtil.getZkClient();
            CuratorUtil.clearAllRegistry(zkClient);
        }));
    }
}
