package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomOwnerResMaxService {
    ResultUtils getOwnerMaxRess(int domId, int ownerId);
}
