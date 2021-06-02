package sheeran.serializer;

import sheeran.remoting.client.dto.RpcRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sheeran.common.CommonSerializer;
import org.apache.log4j.Logger;

import java.io.IOException;


public class JsonSerializer implements CommonSerializer {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final static Logger logger = Logger.getLogger(JsonSerializer.class);

    @Override
    public byte[] serializer(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化发生错误：",e);
            return new byte[0];
        }
    }

    @Override
    public Object deSerializer(byte[] bytes, Class<?> clazz) {
        try {
            Object object = objectMapper.readValue(bytes,clazz);
            if (object instanceof RpcRequest){
                object = handleRequest(object);
            }
            return object;
        } catch (IOException e) {
            logger.error("反序列化发生错误",e);
            return null;
        }
    }

    private Object handleRequest(Object object) throws IOException {
        RpcRequest rpcRequest = (RpcRequest)object;
        for (int i = 0;i<rpcRequest.getParaTypes().length;i++){
            Class<?> clazz = rpcRequest.getParaTypes()[i];
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON_CODE;
    }
}
