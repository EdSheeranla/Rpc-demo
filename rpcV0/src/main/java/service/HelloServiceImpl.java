package service;

import org.apache.log4j.Logger;


public class HelloServiceImpl  implements HelloService {
    private final static Logger logger = Logger.getLogger(HelloServiceImpl.class);

    public String hello(HelloObject object) {
        logger.info("接收到："+object.getMessage());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "这里是用掉的返回值,id = "+object.getId();
    }
}
