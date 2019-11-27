package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

/**
 * 针对对内sql异常处理
 */
public interface UserService {
    ResultUtils addUser(DomOwnerUser domOwnerUser);
    ResultUtils delUser(DomOwnerUser domOwnerUser);
    ResultUtils setAdmin(DomOwnerUser domOwnerUser);
    ResultUtils concelAdmin(DomOwnerUser domOwnerUser);
}
