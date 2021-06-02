package sheeran.common;

public class RpcError {
    public final static String SERVICE_NOT_IMPLEMENT_ANY_INTERFACE = "sheeran.service not implement any interface";
    public final static String SERVICE_NOT_FOUND = "sheeran.service not found";
    public final static String UNKNOWN_PROTOCOL = "unknown protocol";
    public static final String UNKNOWN_PACKAGE = "unknown package";
    public static final String UNKNOWN_SERIALIZER = "unknown sheeran.serializer";

    public static final String REGISTER_SERVICE_FAILED = "register sheeran.service failed";
    public static final String CLIENT_CONNECT_SERVER_FAILURE = "sheeran.remoting.client connect sheeran.remoting.server failure";
    public static final String SERVICE_SCAN_PACKAGE_NOT_FOUND = "sheeran.service scan package not found";
}
