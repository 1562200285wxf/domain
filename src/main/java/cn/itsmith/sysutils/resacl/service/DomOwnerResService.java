package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomOwnerResService {
    //资源树只查询当前属主，不考虑属主迭代
    ResultUtils getOwnerResTree(int domId, int ownerId);

    boolean ownerResExist(int domId, int ownerId, int resTyeId);
}
