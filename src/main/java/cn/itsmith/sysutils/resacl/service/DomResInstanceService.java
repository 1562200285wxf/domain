package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResInstance;

import cn.itsmith.sysutils.resacl.utils.ResultUtils;

import java.util.List;

public interface DomResInstanceService {
    ResultUtils addDomResInstance(DomResInstance domResInstance);
    ResultUtils addDomResInstances(List<DomResInstance> domResInstances);
    ResultUtils deleteDomResInstance(DomResInstance domResInstance);
    ResultUtils getOwnerTreeResInstance(int domId, int ownerId);
    ResultUtils getOwnerInstance(int domId, int ownerId);
    ResultUtils getOwnerRTypeInstance(int domId, int ownerId, int resTypeId);
    boolean resInstanceExist(int domId, int ownerId, int resTypeId, int resId);
    ResultUtils getBaseRTypeInstance(int domId, int ownerId, int resTypeId);

}
