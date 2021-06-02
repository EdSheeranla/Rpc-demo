package sheeran.common;

import sheeran.remoting.client.dto.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAG_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAG_NUMBER);
        if(msg instanceof RpcRequest){
            byteBuf.writeInt(PackageType.REQUEST_PACK);
        }else{
            byteBuf.writeInt(PackageType.RESPONSE_PACK);
        }
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serializer(msg);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
