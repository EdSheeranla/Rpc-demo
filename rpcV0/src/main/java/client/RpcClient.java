package client;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
