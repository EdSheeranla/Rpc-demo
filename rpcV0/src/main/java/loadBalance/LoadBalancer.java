package loadBalance;

import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;

/**
 *  负载均衡算法
 */
public interface LoadBalancer {
    String select(List<String> addressList);
}
