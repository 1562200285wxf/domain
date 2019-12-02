package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

import java.util.List;

@Data
public class DomResOwnerUU {
    Integer domId;
    Integer ownerId;
    String ownerDes;
    Integer pId;
    List<DomOwnerUser> ownerUsers;

    Integer status;
    String ownerName;
}
