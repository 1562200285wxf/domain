package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

import java.util.List;

/**
 * 针对对内sql异常处理
 */
public interface UserService {
    ResultUtils addUser(DomOwnerUser domOwnerUser);
    ResultUtils delUser(DomOwnerUser domOwnerUser);
    ResultUtils setAdmin(DomOwnerUser domOwnerUser);
    ResultUtils concelAdmin(DomOwnerUser domOwnerUser);
    //查询在特定属主下的domUsers
    ResultUtils queryUsers(DomOwnerUser domOwnerUser);
    //查询特定属主下不存在的基本Users
    ResultUtils queryBaseUsers(DomOwnerUser domOwnerUser);
    //添加基本UserList
    ResultUtils addUsersFromBase(List<DomOwnerUser> domOwnerUsers);

}
