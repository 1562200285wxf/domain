package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

/**
 * 针对对内sql异常处理
 */
public interface ResService {
    ResultUtils addRes(DomOwnerRes domOwnerRes);
    ResultUtils delRes(DomOwnerRes domOwnerRes);
    //从基本表中查询特定属主下不存在的资源类型
    ResultUtils getResWithoutOwner(Integer domId,Integer ownerId);
}
