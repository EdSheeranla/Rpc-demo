package common;

public interface CommonSerializer {
    int getCode();

    byte[] serializer(Object object);

    Object deSerializer(byte[] bytes, Class<?> clazz);

    static CommonSerializer getByCode(int code){
        switch (code){
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
