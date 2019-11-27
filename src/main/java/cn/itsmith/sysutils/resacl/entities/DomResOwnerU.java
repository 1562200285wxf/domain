package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;
/**
 * 用于DomResOwnerController更新的工具类
 */
@Data
public class DomResOwnerU {
        Integer domId;
        Integer ownerId;
        String ownerDes;
}
