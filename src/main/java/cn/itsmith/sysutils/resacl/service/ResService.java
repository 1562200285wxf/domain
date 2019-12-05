package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

import java.util.List;

/**
 * 针对对内sql异常处理
 */
public interface ResService {
    ResultUtils addRes(DomOwnerRes domOwnerRes);
    ResultUtils delRes(DomOwnerRes domOwnerRes);
    //从基本表中查询特定属主下不存在的资源类型
    ResultUtils getResWithoutOwner(Integer domId,Integer ownerId);
    //添加基本ResList
    ResultUtils addResFromBase(List<DomOwnerRes> domOwnerRes);
}
