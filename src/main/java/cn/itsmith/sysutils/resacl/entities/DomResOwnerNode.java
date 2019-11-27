package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DomResOwnerNode {
    DomResOwner data;
    List<DomResOwnerNode> childList;

    public DomResOwnerNode(DomResOwner data) {
        this.data = data;
        this.childList = new ArrayList<DomResOwnerNode>();
    }

    public DomResOwnerNode(DomResOwner data, List<DomResOwnerNode> childList) {
        this.data = data;
        this.childList = childList;
    }
}
