package client;

import client.dto.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
