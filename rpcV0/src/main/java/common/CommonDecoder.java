package common;

import client.dto.RpcRequest;
import exceptions.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.log4j.Logger;
import server.dto.RpcResponse;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {
    private static final int MAG_NUMBER = 0xCAFEBABE;
    private final static Logger logger = Logger.getLogger(CommonDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic = byteBuf.readInt();
        if (magic != MAG_NUMBER) {
            logger.error("不识别的协议包: "+magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK){
            packageClass = RpcResponse.class;
        }else{
            logger.error("不识别的数据包: "+packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE);
        }
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null){
            logger.error("不识别的解析器: "+serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object object = serializer.deSerializer(bytes,packageClass);
        list.add(object);
    }
}
