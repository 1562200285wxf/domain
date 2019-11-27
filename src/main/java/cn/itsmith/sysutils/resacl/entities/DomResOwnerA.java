package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

/**
 * 用于DomResOwnerController添加的工具类
 */
@Data
public class DomResOwnerA {
    Integer domId;
    String ownerDes;
    String ownerName;
    //父节点=0又是根节点。
    Integer pId;
}
