package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResOwner;
import cn.itsmith.sysutils.resacl.entities.DomResOwnerNode;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomResOwnerService {
    ResultUtils getAllDomResOwner();
    ResultUtils addDomResOwner(DomResOwner domResOwner);
    ResultUtils updateOwnerDes(DomResOwner domResOwner);
    ResultUtils deleteDomResOwner(DomResOwner domResOwner);
    boolean ownerExist(int domId, int ownerId);

    void ownerUsing(int domId, int ownerId);
    DomResOwnerNode createDomResOwnerTree(DomResOwnerNode domResOwnerNode);
    ResultUtils getDomResOwnerTree(int domId, int ownerId);
}
