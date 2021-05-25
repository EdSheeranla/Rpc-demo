package server.netty;

import client.dto.RpcRequest;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import server.handler.RequestHandler;
import server.dto.RpcResponse;
import util.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = Logger.getLogger(NettyServerHandler.class);

    private static RequestHandler requestHandler;

    private static final ExecutorService threadPool;

    private static final String THREAD_NAME_PREFIX = "netty-server-handler";


    static{
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        logger.info("服务开始处理请求:"+rpcRequest);
        threadPool.execute(()->{
            try{
                logger.info("服务器接收到请求:"+rpcRequest);
                Object result = requestHandler.handle(rpcRequest);
                ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(RpcResponse.success(result,rpcRequest.getRequestId()));
                channelFuture.addListener(ChannelFutureListener.CLOSE);

            }finally {
                ReferenceCountUtil.release(rpcRequest);
            }
        });

    }
}
