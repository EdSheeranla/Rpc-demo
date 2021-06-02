package sheeran.loadBalance;

import java.util.List;

/**
 *  负载均衡算法
 */
public interface LoadBalancer {
    String select(List<String> addressList);
}
