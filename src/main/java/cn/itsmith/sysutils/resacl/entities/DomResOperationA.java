package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;
/**
 * 用于DomResOperationController添加和删除的工具类
 */
@Data
public class DomResOperationA {
    Integer domId;
    Integer resTypeId;
    Integer opId;
    String opDes;
    Integer isExtend;
    Integer isCommon;
}
