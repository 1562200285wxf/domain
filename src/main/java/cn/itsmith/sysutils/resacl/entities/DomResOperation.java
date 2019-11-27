package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//资源可用权限：针对域下资源，将资源划分为等级。
@Data
public class DomResOperation {
    Integer id;
    Integer domId;
    Integer resTypeId;
    Integer opId;
    String opDes;
    Integer isExtend;
    Integer isCommon;
    Integer status;
}
