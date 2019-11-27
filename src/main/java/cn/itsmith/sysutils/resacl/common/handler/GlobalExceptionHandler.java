package cn.itsmith.sysutils.resacl.common.handler;

import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 业务的异常处理
     *
     * 控制器的其他函数只负责关注正常的业务逻辑，所以异常逻辑由本函数完成
     *
     * @param e
     * @return
     */
    @ExceptionHandler(FailedException.class)
    public ResponseEntity<?> Failed(FailedException e){
        Map error = new HashMap();
        error.put("code",e.getCode());
        error.put("message",e.getMessage());
        error.put("data",new ArrayList<>());
        return new ResponseEntity<Map>(error, HttpStatus.BAD_REQUEST);
    }

}
