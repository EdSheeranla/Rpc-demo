package client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {
    //调用方法名
    private String methodName;
    //    调用接口名
    private String interfaceName;
    //    调用方法参数实际值
    private Object[] parameters;
    //调用方法的参数类型
    private Class<?>[] paraTypes;

    private RpcRequest(Builder builder) {
        this.methodName = builder.methodName;
        this.interfaceName = builder.interfaceName;
        this.parameters = builder.parameters;
        this.paraTypes = builder.paraTypes;

    }

    public static Builder build(){
        return new Builder();
    }
    public static class Builder {
        private String methodName;
        //    调用接口名
        private String interfaceName;
        //    调用方法参数实际值
        private Object[] parameters;
        //调用方法的参数类型
        private Class<?>[] paraTypes;


        public Builder interfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
            return this;
        }

        public Builder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder parameters(Object[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder paraType(Class<?>[] paraTypes) {
            this.paraTypes = paraTypes;
            return this;
        }

        public RpcRequest build() {
            return new RpcRequest(this);
        }
    }
}
