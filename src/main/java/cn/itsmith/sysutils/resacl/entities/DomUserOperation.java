package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//成员已授权限
@Data
public class DomUserOperation {
    Integer domId;
    Integer resTypeId;
    Integer resId;
    Integer ownerId;
    Integer opId;
    Integer userOwnerId;
    Integer id;
    Integer types;
    Integer status;
    String opName;
}
