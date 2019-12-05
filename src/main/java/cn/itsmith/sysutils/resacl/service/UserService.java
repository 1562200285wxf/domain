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

    /**
     * 用户添加模块1
     *
     */
    //查询特定属主下不存在的基本Users
    ResultUtils queryBaseUsers(DomOwnerUser domOwnerUser);
    //添加基本UserList
    ResultUtils addUsersFromBase(List<DomOwnerUser> domOwnerUsers);
    /**
     * 用户添加模块2
     *
     */
    //查询其他属主下的所有成员，且本属主下不存在的成员
    ResultUtils queryOtherUsers(DomOwnerUser domOwnerUser);
/**
 * 添加模块3
 * 一个新的界面
 * 点击机构后的简单成员查询，剔除已经有的
 */
ResultUtils queryAnyOtherUsers(DomOwnerUser domOwnerUser);


}
