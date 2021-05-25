package client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import server.dto.RpcResponse;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private final static Logger logger = Logger.getLogger(NettyClientHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {
        logger.info(String.format("客户端接收到消息: %s", msg));
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"+msg.getRequestId());
        channelHandlerContext.channel().attr(key).set(msg);
        channelHandlerContext.channel().close();
    }
}
