package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DomResOwnerRMax {
//    Integer domId;
//    Integer ownerId;
//    String ownerDes;
//    Integer pId;
    DomResOwner data;
    List<DomResType> domResTypes;//【max新树】

    public DomResOwnerRMax(DomResOwner data){
        this.data = data;
        this.domResTypes  = new ArrayList<DomResType>();
}
}
