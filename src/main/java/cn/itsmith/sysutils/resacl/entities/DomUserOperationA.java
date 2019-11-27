package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

@Data
public class DomUserOperationA {
    Integer domId;
    Integer resTypeId;
    Integer resId;
    Integer ownerId;
    Integer opId;
    Integer userOwnerId;
    Integer types;
}
