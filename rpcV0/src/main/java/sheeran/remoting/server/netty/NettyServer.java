package sheeran.remoting.server.netty;

import sheeran.common.*;
import sheeran.hook.ShutDownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import sheeran.loadBalance.RoundRobinLoadBalancer;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import sheeran.provider.ServiceProvider;
import sheeran.registry.ServiceRegistry;
import sheeran.serializer.KryoSerializer;
import sheeran.provider.ServiceProviderImpl;
import sheeran.registry.ZKServiceRegistry;
import sheeran.spring.RpcService;

import java.net.InetSocketAddress;
import java.util.Map;

@Component
@PropertySource("classpath:rpc.properties")
public class NettyServer implements InitializingBean, ApplicationContextAware {
    private final static Logger logger = Logger.getLogger(NettyServer.class);
    @Value("${rpc.server.host}")
    private String host;

    @Value("${rpc.server.port}")
    private int port;


    private final ServiceRegistry serviceRegistry = new ZKServiceRegistry(new RoundRobinLoadBalancer());
    private final ServiceProvider serviceProvider = new ServiceProviderImpl();

    public void publishService(Object service,Class<?> serviceClass){
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(),new InetSocketAddress(host, port));
    }
    public void start() {
        logger.info("启动服务器中........");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            ShutDownHook.getInstance().addClearAllHook();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("服务器启动出现错误", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ShutDownHook.getInstance().addClearAllHook();;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> registerBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        registerBeanMap.values().forEach(o -> publishService(o,o.getClass().getInterfaces()[0]));
    }



}

