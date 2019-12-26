package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

/**
 * 用于DomResOwnerController删除的工具类
 */
@Data
public class DomResOwnerD {
    Integer domId;
    Integer ownerId;
    Integer pId;
}
