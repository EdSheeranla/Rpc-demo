package sheeran.remoting.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import sheeran.remoting.server.netty.NettyServer;
import sheeran.spring.RpcServiceScan;

//@ServiceScan(value = "sheeran.service")
@RpcServiceScan(serviceBasePackage = {"sheeran"})
public class serverTest {

public static void main(String[] args) {
    AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(serverTest.class);
    String[] beans = app.getBeanDefinitionNames();

    for (String beanName : beans){
        System.out.println(beanName);
    }
    NettyServer nettyServer = (NettyServer) app.getBean("nettyServer");
    nettyServer.start();
}
}
