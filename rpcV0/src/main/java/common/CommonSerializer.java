package common;

import serializer.JsonSerializer;
import serializer.KryoSerializer;
import serializer.SerializerCode;

public interface CommonSerializer {

    int getCode();

    byte[] serializer(Object object);

    Object deSerializer(byte[] bytes, Class<?> clazz);

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case SerializerCode.JSON_CODE:
                return new JsonSerializer();
            default:
                return new KryoSerializer();
        }
    }
}
