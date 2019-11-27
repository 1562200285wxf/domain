package cn.itsmith.sysutils.resacl.common.utilss;

import java.util.Random;

public class TokenUtil {
    private  int length = 10;
    public  String getRandom() {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }
}
