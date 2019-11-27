package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResInstance;

import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomResInstanceService {
    ResultUtils addDomResInstance(DomResInstance domResInstance);
    ResultUtils deleteDomResInstance(DomResInstance domResInstance);
    ResultUtils getOwnerResInstance(int domId, int ownerId);
    boolean resInstanceExist(int domId, int ownerId, int resTypeId, int resId);

}
