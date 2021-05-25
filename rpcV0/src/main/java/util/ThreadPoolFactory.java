package util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolFactory {

    private static final int CORE_POOL_SIZE = 10;

    private static final int MAX_POOL_SIZE = 100;

    private static final int KEEP_ALIVE_TIME = 1;

    private static final int BLOCK_QUEUE_CAPACITY = 100;

    private final static Logger logger = Logger.getLogger(ThreadPoolFactory.class);

    private final static Map<String,ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix){
        return createDefaultThreadPool(threadNamePrefix, false);
    }


    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon){
        ExecutorService pool = threadPoolMap.computeIfAbsent(threadNamePrefix, k->createThreadPool(threadNamePrefix,daemon));
        if (pool.isShutdown() || pool.isTerminated()){
            threadPoolMap.remove(threadNamePrefix);
            pool = createDefaultThreadPool(threadNamePrefix,daemon);
            threadPoolMap.put(threadNamePrefix,pool);
        }
        logger.info("创建自建线程池成功");
        return pool;
    }

    private static ExecutorService createThreadPool(String threadNamePrefix,Boolean daemon){
        BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(BLOCK_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix,daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.MINUTES,taskQueue,threadFactory);
    }

    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            logger.info("创建自建线程池: "+threadNamePrefix);
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static  void shutDownAll(){
        logger.info("关闭所有线程池");
        threadPoolMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            try {
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("关闭线程池失败");
                executorService.shutdownNow();
                e.printStackTrace();

            }
        });
    }
}
