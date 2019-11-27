package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.*;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomOwnerUserService {
//    boolean isOwnerAdmin(Domain domain, DomResOwner domResOwner, DomOwnerUser domOwnerUser);
    boolean userExist(int domId, int ownerId, int userId);

    //liu
    //查询属主树仅需要这两个id就够了
    //至于成员树或者种类树为了不改动原有查询属主树的接口就不额外传id了
    //可以根据这两个已有的id从成员逻辑表或者种类逻辑表中获取成员list或种类list,如果该属主下查不出成员list或种类list，service里面直接抛异常
    //controller再另外调用（domId,ownerId,userId/ownerId）接口查询是否存在前端输入待查询的该成员/种类，如果没有同样要抛异常且一定要抛【与service里的异常不重复】
    ResultUtils getOwnerUserTree(int domId, int ownerId);
}
