package server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;

//    Jackson反序列化需要添加一个无参构造器
    public HelloObject() {
    }
}
