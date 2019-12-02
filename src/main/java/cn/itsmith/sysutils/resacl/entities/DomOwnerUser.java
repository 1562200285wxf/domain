package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//属主成员
@Data
public class DomOwnerUser {
    Integer id;
    Integer domId;
    Integer ownerId;
    Integer userId;
    //  是本域，本属主中。0代表普通成员 1代表管理员
    Integer isAdmin;
    Integer status;
    String userName;

}
