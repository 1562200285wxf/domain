package cn.itsmith.sysutils.resacl.utils;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DomResOwnerRNode {
    DomOwnerRes data;
    List<DomResOwnerRNode> childList;

    public DomResOwnerRNode(DomOwnerRes data) {
        this.data = data;
        this.childList = new ArrayList<DomResOwnerRNode>();
    }

    public DomResOwnerRNode(DomOwnerRes data, List<DomResOwnerRNode> childList) {
        this.data = data;
        this.childList = childList;
    }
}
