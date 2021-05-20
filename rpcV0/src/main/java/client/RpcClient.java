package client;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {
    private static  final Logger logger =  Logger.getLogger(RpcClient.class);

    public Object sendRequest (RpcRequest rpcRequest, String host, Integer port){
        try {
            Socket socket = new Socket(host,port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException e) {
            logger.error("调用时发生错误:",e);
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("调用时发生错误:",e);
            e.printStackTrace();
            return null;
        }
    }
}
