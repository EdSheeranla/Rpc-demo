package client;

import common.CommonDecoder;
import common.CommonEncoder;
import common.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import server.RpcResponse;

public class NettyClient implements RpcClient {
    private final static Logger logger = Logger.getLogger(NettyClient.class);
    private String host ;
    private int port ;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static{
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }
    @Override

    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host,port).sync();
            logger.info("客户端连接到服务器: "+host+"  :  "+port);
            Channel channel = future.channel();
            if (channel!=null){
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                   if (future1.isSuccess()){
                       logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                   }else{
                       logger.error("发送消息时有错误发生：",future.cause());
                   }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
