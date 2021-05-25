package serializer;

import client.dto.RpcRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.CommonSerializer;
import exceptions.SerializerException;
import org.apache.log4j.Logger;
import server.dto.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {
    private static final Logger logger = Logger.getLogger(KryoSerializer.class);

    private final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>(){
        protected Kryo initialValue(){
            Kryo kryo = new Kryo();

            kryo.register(RpcResponse.class);
            kryo.register(RpcRequest.class);
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };
    @Override
    public byte[] serializer(Object object) {
        try{
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,object);
            kryoThreadLocal.remove();
            return output.toBytes();

        }catch (Exception e){
            logger.error("序列化发生错误",e);
            throw new SerializerException("序列化发生错误");
        }
    }

    @Override
    public Object deSerializer(byte[] bytes, Class<?> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input,clazz);
            kryoThreadLocal.remove();
            return o;
        }catch (Exception e){
            logger.error("反序列化发生错误",e);
            throw new SerializerException("反序列化发生错误");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO_CODE;
    }
}
