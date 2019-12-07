package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

@Data
public class DomOwnerUserB {
    Integer id;
    Integer domId;
    Integer ownerId;
    Integer userId;
    Integer isAdmin;
    Integer status;
    String userName;
    //
    String ownerName;
}
