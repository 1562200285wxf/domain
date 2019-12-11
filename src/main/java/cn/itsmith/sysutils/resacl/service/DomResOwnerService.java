package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResOperationL;
import cn.itsmith.sysutils.resacl.entities.DomResOwner;
import cn.itsmith.sysutils.resacl.entities.DomResOwnerNode;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

import java.util.List;

public interface DomResOwnerService {
    ResultUtils getAllDomResOwner();
    ResultUtils addDomResOwner(DomResOwner domResOwner);
    ResultUtils updateOwnerDes(DomResOwner domResOwner);
    ResultUtils deleteDomResOwner(DomResOwner domResOwner);
    ResultUtils getOperationOwners(List<DomResOperationL> domResOperationLS);
    boolean ownerExist(int domId, int ownerId);

    void ownerUsing(int domId, int ownerId);
    DomResOwnerNode createDomResOwnerTree(DomResOwnerNode domResOwnerNode);
    ResultUtils getDomResOwnerTree(int domId, int ownerId);
}
