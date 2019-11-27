package cn.itsmith.sysutils.resacl.utils;

//import org.apache.commons.lang3.StringUtils;
public class ResultUtils {
    public static final String RESULT_SUCCESS = "success";

    private int code;
    private String message;
    private Object data;

    public ResultUtils(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultUtils(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultUtils() {

    }

    public int getCode() {
//        if (code == 0) {
//            code = 200;
//        }
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
//        if (StringUtils.isBlank(message)) {
//            message = RESULT_SUCCESS;
//        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
