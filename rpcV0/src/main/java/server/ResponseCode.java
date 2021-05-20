package server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCode {
    public static final ResponseCode METHOD_NOT_FOUND = new ResponseCode(501,"Method not found");

    final static Integer SUCCESS = 200;

    final static Integer FAIL = 404;

    private Integer code;

    private String message;


}
