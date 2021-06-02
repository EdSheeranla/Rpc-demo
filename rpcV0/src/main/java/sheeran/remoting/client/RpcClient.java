package sheeran.remoting.client;

import sheeran.remoting.client.dto.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
