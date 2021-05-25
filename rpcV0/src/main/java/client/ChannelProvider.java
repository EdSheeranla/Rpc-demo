package client;

import client.netty.NettyClientHandler;
import common.CommonDecoder;
import common.CommonEncoder;
import common.CommonSerializer;
import common.RpcError;
import exceptions.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private final static Logger logger = Logger.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootStrap();

    private static Channel channel;
    private final static int MAX_RETRY = 5;

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try{
            connect(bootstrap,inetSocketAddress,countDownLatch);
            countDownLatch.await();
        }catch (Exception e){
            logger.error("获取channel出错",e);
        }
        logger.info("获取channel成功"+channel.id().toString() );
        return channel;

    }
    private static void connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress,CountDownLatch countDownLatch){
        connect(bootstrap,inetSocketAddress,MAX_RETRY,countDownLatch);
    }

    private static void connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress,int retry ,CountDownLatch countDownLatch){
            bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future->{
                if (future.isSuccess()){
                    logger.info("客户端连接成功");
                    channel = future.channel();
                    countDownLatch.countDown();
                    return;
                }
                if(retry == 0){
                    logger.error("客户端连接失败，尝试次数超过上限，解除连接");
                    countDownLatch.countDown();
                    throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
                }
                //重连次数
                int order = MAX_RETRY - retry+1;
                //重连的间隔
                int delay = 1<<order;
                logger.error("连接失败，第"+order+"次重连");
                bootstrap.config().group().schedule(()->connect(bootstrap,inetSocketAddress,retry-1,countDownLatch),delay, TimeUnit.SECONDS);

            });

    }

    private static Bootstrap initializeBootStrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
