package sheeran.loadBalance;

import java.util.List;

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
