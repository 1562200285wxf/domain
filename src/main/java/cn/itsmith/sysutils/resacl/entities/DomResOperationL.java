package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

@Data
public class DomResOperationL {
    Integer domId;
    Integer ownerId;
    Integer resTypeId;
    Integer resId;
    Integer opId;


}
