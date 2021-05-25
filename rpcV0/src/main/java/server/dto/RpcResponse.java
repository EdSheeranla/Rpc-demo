package server.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {

    //    响应状态码
    private Integer statusCode;
    //    响应信息
    private String message;
    //    响应数据
    private T data;

    private Integer requestId;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(ResponseCode.SUCCESS);
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> success(T data,int requestId) {
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(ResponseCode.SUCCESS);
        response.setData(data);
        response.setRequestId(requestId);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code) {
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
