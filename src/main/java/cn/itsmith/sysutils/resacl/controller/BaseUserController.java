package cn.itsmith.sysutils.resacl.controller;
import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerUserMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomainMapper;
import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.entities.DomOwnerUserA;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.service.UserService;
import cn.itsmith.sysutils.resacl.serviceImpl.DomOwnerUserServiceImpl;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value="基本成员Controller",tags={"基本成员Api"})
@RestController

public class BaseUserController {

    @Autowired
    DomainMapper domainMapper;
    @Autowired
    DomResOwnerService domResOwnerService1;
    @Autowired
    UserService userService;

    @ApiOperation(value = "查询特定属主下不存在的基本成员们", notes = "针对属主成员的查询操作")
    @RequestMapping(value="/querybaseuserswithoutdom/{domId}",method = RequestMethod.GET)
    public ResultUtils queryBaseUsersWithoutDom(@PathVariable Integer domId,
                                                @RequestParam(value="ownerId",required = true) Integer ownerId,
                                                @RequestHeader(value = "Validate", required = true) String validate) {

        Integer varifycode  = domainMapper.varify(domId,validate);
        DomOwnerUser domOwnerUser = new DomOwnerUser();
        domOwnerUser.setDomId(domId);
        domOwnerUser.setOwnerId(ownerId);

        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "查询特定属主下不存在的基本成员们,失败，"+
                            ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else if(!(domResOwnerService1.ownerExist(domId,ownerId))){
            throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(),
                    "查询失败，因为域"+domId+"下的属主"+ownerId+"不存在"
            );
        }else {
            return userService.queryBaseUsers(domOwnerUser);
        }
    }

}

