package sheeran.remoting.server;

import sheeran.remoting.client.dto.RpcRequest;
import org.apache.log4j.Logger;
import sheeran.remoting.server.dto.RpcResponse;
import sheeran.remoting.server.handler.RequestHandler;
import sheeran.provider.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Deprecated
public class RequestHandlerThread implements Runnable {
    private final Logger logger = Logger.getLogger(RequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;


    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceProvider serviceProvider) {
        this.socket = socket;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = requestHandler.handle(rpcRequest);

            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
