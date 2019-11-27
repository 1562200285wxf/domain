package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

import java.util.List;
@Data
public class DomResOwnerR {
    Integer domId;
    Integer ownerId;
    String ownerDes;
    Integer pId;
    List<DomOwnerRes> ownerRes;
}
