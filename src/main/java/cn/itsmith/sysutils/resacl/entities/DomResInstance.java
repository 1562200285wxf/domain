package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//资源实例；
@Data
public class DomResInstance {
    Integer id;
    Integer domId;
    Integer ownerId;
    Integer resId;
    Integer resTypeId;
    //资源父实例id
    Integer resPid;
    Integer status;
//    String insName;
}
