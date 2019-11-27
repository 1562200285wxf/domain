package cn.itsmith.sysutils.resacl.utils;

import cn.itsmith.sysutils.resacl.entities.DomResOwnerUU;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DomResOwnerUNode {
    DomResOwnerUU data;
    List<DomResOwnerUNode> childList;
    public DomResOwnerUNode(DomResOwnerUU data) {
        this.data = data;
        this.childList = new ArrayList<DomResOwnerUNode>();
    }

    public DomResOwnerUNode(DomResOwnerUU data, List<DomResOwnerUNode> childList) {
        this.data = data;
        this.childList = childList;
    }
}
