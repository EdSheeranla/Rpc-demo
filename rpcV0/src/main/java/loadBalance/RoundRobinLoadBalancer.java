package loadBalance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoundRobinLoadBalancer implements LoadBalancer {
//    private final ConcurrentHashMap<String, AtomicInteger> sequences = new ConcurrentHashMap<>();
//    private AtomicInteger current = new AtomicInteger(0);

    private int index = 0;
    @Override
    public String select(List<String> addressList) {
        if (index >= addressList.size()){
            index %= addressList.size();
        }
        return addressList.get(index++);
    }
}
