package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//资源属主：认为是域下用户的集合，按照节点往下分叉。
@Data
public class DomResOwner {
    Integer domId;
    Integer ownerId;
    String ownerDes;
    //父节点=0又是根节点。
    Integer pId;
    String ownerName;
    Integer status;
}
