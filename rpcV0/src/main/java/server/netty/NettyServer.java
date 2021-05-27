package server.netty;

import common.*;
import hook.ShutDownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import loadBalance.LoadBalancer;
import org.apache.log4j.Logger;
import serializer.KryoSerializer;
import server.AbstractRpcServer;
import server.RpcServer;
import server.provider.ServiceProvider;
import server.provider.ServiceProviderImpl;
import registry.ServiceRegistry;
import registry.ZKServiceRegistry;

import java.net.InetSocketAddress;


public class NettyServer extends AbstractRpcServer {
    private final static Logger logger = Logger.getLogger(NettyServer.class);
//    private final String host;
//    private final int port;
//    private final ServiceRegistry serviceRegistry;
//    private final ServiceProvider serviceProvider;
//    private CommonSerializer serializer;

    public NettyServer(String host, int port, LoadBalancer loadBalancer) {
        super.host = host;
        super.port = port;
        super.serviceRegistry = new ZKServiceRegistry(loadBalancer);
        super.serviceProvider = new ServiceProviderImpl();
        scanServices();
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

//    @Override
//    public <T> void publishService(Object service, String serviceName) {
//
//        serviceProvider.addServiceProvider(service);
//        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
//    }


}

