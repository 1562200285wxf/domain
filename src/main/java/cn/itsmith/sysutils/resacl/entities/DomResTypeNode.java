package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class DomResTypeNode {

    DomResType data;
    List<DomResTypeNode> childList;

    public DomResTypeNode(DomResType data) {
        this.data = data;
        this.childList = new ArrayList<DomResTypeNode>();
    }

    public DomResTypeNode(DomResType data, List<DomResTypeNode> childList) {
        this.data = data;
        this.childList = childList;
    }
}
