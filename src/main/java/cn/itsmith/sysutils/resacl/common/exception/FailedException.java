package cn.itsmith.sysutils.resacl.common.exception;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;

public class FailedException extends RuntimeException {

    private int code;
    private String message;

    public FailedException(String msg) {
        this.code = ResponseInfo.UNKNOWN_ERROR.getErrorCode();
        this.message = msg;
    }

    public FailedException(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public FailedException(ResponseInfo responseInfo) {
        this.code = responseInfo.getErrorCode();
        this.message = responseInfo.getErrorMsg();
    }

    public int getCode(){
        return this.code;
    }

    public void setCode(int code){
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
