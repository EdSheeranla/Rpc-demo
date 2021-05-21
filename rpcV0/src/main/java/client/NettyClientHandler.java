package client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;
import server.RpcResponse;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private final static Logger logger = Logger.getLogger(NettyClientHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {
        logger.info(String.format("客户端接收到消息: %s", msg));
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        channelHandlerContext.channel().attr(key).set(msg);
        channelHandlerContext.channel().close();
    }
}
