package sheeran.remoting.client.netty;

import sheeran.remoting.client.RpcClient;
import sheeran.remoting.client.dto.RpcRequest;
import sheeran.common.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import sheeran.loadBalance.LoadBalancer;
import sheeran.loadBalance.RoundRobinLoadBalancer;
import org.apache.log4j.Logger;
import sheeran.serializer.KryoSerializer;
import sheeran.remoting.server.dto.RpcResponse;
import sheeran.registry.ServiceRegistry;
import sheeran.registry.ZKServiceRegistry;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    private final static Logger logger = Logger.getLogger(NettyClient.class);
    private static final Bootstrap bootstrap;
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    static {

        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient() {
        this.serviceRegistry = new ZKServiceRegistry(new RoundRobinLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer){
        this.serviceRegistry = new ZKServiceRegistry(loadBalancer);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        logger.info("客户端发送消息中.....");
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, new KryoSerializer());
            if (channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生：", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"+rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                result.set(rpcResponse.getData());
                return rpcResponse.getData();
            }else{
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.error("发送消息有错误发生",e);
        }
        return result.get();
    }
}
