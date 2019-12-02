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

@Api(value="成员Controller",tags={"属主拥有成员Api"})
@RestController
public class UserController {
    @Autowired
    DomOwnerUserMapper userMapper;
    @Autowired
    DomainMapper domainMapper;
    @Autowired
    DomResOwnerMapper ownerMapper;
    @Autowired
    DomResOwnerService domResOwnerService1;

@Autowired
UserService userService;

    @ApiOperation(value = "成员添加", notes = "针对属主成员的添加操作  \n" +
            "添加成功代码：200  \n"+
            "添加成功要求列举:  \n"+
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.目标插入到的属主一定要存在【对应异常代码：1002】  \n" +
            "3.不能重复添加一摸一样的一条用户记录【对应异常代码：1003】  \n" +
            "4.用户基本表中一定要有目标用户才能向逻辑表中添加【对应异常代码：1004】  \n"

    )
    @RequestMapping(value="/adddomuser",method = RequestMethod.POST)
    public ResultUtils addDomUser(@RequestBody DomOwnerUserA domOwnerUserA, @RequestHeader(value = "token", required = true) String validate){
        DomOwnerUser domOwnerUser = new DomOwnerUser();
        domOwnerUser.setDomId(domOwnerUserA.getDomId());
        domOwnerUser.setOwnerId(domOwnerUserA.getOwnerId());
        domOwnerUser.setUserId(domOwnerUserA.getUserId());
        Integer varifycode  = domainMapper.varify(domOwnerUser.getDomId(),validate);
        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能将用户"+domOwnerUserA.getUserId()+"加入到域"+domOwnerUserA.getDomId()+
                            "下，因为"+ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else{
            return userService.addUser(domOwnerUser);
        }

    }



    //删除
    @ApiOperation(value = "成员删除", notes = "针对属主成员的删除操作  \n"+
            "删除成功代码：200  \n" +
            "删除成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.成员一定要存在于你所输入的域下的属主下才能删除【对应异常代码：1006】  \n"
    )
    @RequestMapping(value="/removedomuser",method = RequestMethod.POST)
    public ResultUtils removeUser(@RequestBody DomOwnerUserA domOwnerUserA, @RequestHeader(value = "Validate", required = true) String validate){
        DomOwnerUser domOwnerUser = new DomOwnerUser();
        //三个id唯一标识一个正儿八经的DomOwnerUser
        domOwnerUser.setDomId(domOwnerUserA.getDomId());
        domOwnerUser.setOwnerId(domOwnerUserA.getOwnerId());
        domOwnerUser.setUserId(domOwnerUserA.getUserId());
        Integer varifycode  = domainMapper.varify(domOwnerUserA.getDomId(),validate);

        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能删除成员"+domOwnerUserA.getUserId()+
                            "，因为"+ ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else{
            return userService.delUser(domOwnerUser);
        }

    }
    //管理员

    @ApiOperation(value = "设置管理员", notes = "针对设置属主成员为管理员的操作  \n" +
            "设置成功代码：200  \n" +
            "设置成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.成员一定要存在于你所输入的域下的属主下才能设置【对应异常代码：1007】  \n" +
            "3.成员只有是非管理员的情况下才能被设置为管理员【对应异常代码：1009】"
    )
    @RequestMapping(value="/setadmin",method = RequestMethod.POST)
    public ResultUtils setAdmin(@RequestBody DomOwnerUserA domOwnerUserA, @RequestHeader(value = "Validate", required = true) String validate){
        DomOwnerUser domOwnerUser = new DomOwnerUser();
        domOwnerUser.setDomId(domOwnerUserA.getDomId());
        domOwnerUser.setOwnerId(domOwnerUserA.getOwnerId());
        domOwnerUser.setUserId(domOwnerUserA.getUserId());

        Integer varifycode  = domainMapper.varify(domOwnerUserA.getDomId(),validate);
        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能设置域"+domOwnerUser.getDomId()+"属主"+domOwnerUser.getOwnerId()+"下的成员"+
                            domOwnerUser.getUserId()+
                            "为管理员，因为"+ ResponseInfo.AUTH_FAILED.getErrorMsg());
        }
        else{

            return userService.setAdmin(domOwnerUser);
        }


    }
    @ApiOperation(value = "取消管理员", notes = "针对取消属主成员的管理员身份的操作  \n" +
            "取消成功代码：200  \n" +
            "取消成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.成员一定要存在于你所输入的域下的属主下才能取消【对应异常代码：1008】  \n" +
            "3.成员只有是管理员的情况下才能被取消管理员【对应异常代码：1010】"
    )
    @RequestMapping(value="/revokeadmin",method = RequestMethod.POST)
    public ResultUtils revokeAdmin(@RequestBody DomOwnerUserA domOwnerUserA, @RequestHeader(value = "Validate", required = true) String validate){
        DomOwnerUser domOwnerUser = new DomOwnerUser();
        domOwnerUser.setDomId(domOwnerUserA.getDomId());
        domOwnerUser.setOwnerId(domOwnerUserA.getOwnerId());
        domOwnerUser.setUserId(domOwnerUserA.getUserId());

        Integer varifycode  = domainMapper.varify(domOwnerUserA.getDomId(),validate);
        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能取消域"+domOwnerUser.getDomId()+"属主"+domOwnerUser.getOwnerId()+"下的成员"+
                            domOwnerUser.getUserId()+
                            "的管理员身份，因为"+
                    ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else {
            return userService.concelAdmin(domOwnerUser);
        }

    }
//查询自此结点之下成员树
    @Autowired
DomOwnerUserServiceImpl domResOwnerService;
@ApiOperation(value = "查询属主拥有成员树", notes = "针对属主成员的查询操作  \n" +
        "查询成功代码：200  \n" +
        "查询成功要求列举:  \n" +
        "1.域和token一定要匹配【对应异常代码：1001】  \n" +
        "2.你输入的域下此属主不存在【对应异常代码：1002】  \n"
    )


@RequestMapping(value="/queryusertree",method = RequestMethod.GET)
public ResultUtils queryUserTree(@RequestParam(value="domId",required = true) Integer domId,
                                 @RequestParam(value="ownerId",required = true) Integer ownerId,
                                 @RequestHeader(value = "Validate", required = true) String validate){
    //验证varify
    //属主是否存在
    Integer varifycode  = domainMapper.varify(domId,validate);
    //List<Integer> listOwnerIds = ownerMapper.queryAllOwnerId(domId);
    boolean hasOwner = false;

//    for (Integer id : listOwnerIds) {
//        if(ownerId==id){
//            hasOwner=true;
//            break;
//        }
//    }

    if(varifycode!=1){
        throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                "不能查询域"+domId+"下的属主"+ownerId+"拥有的成员树，因为"+
                ResponseInfo.AUTH_FAILED.getErrorMsg());
    }else if(!(domResOwnerService1.ownerExist(domId,ownerId))){
        throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(),
                "查询失败，因为域"+domId+"下的属主"+ownerId+"不存在"
                );
    }else{

        return domResOwnerService.getOwnerUserTree(domId,ownerId);
    }

    }

    @ApiOperation(value = "查询特定属主下的成员们", notes = "针对属主成员的查询操作")
    @RequestMapping(value="/queryusers/{domId}",method = RequestMethod.GET)
    public ResultUtils queryUsers(@PathVariable Integer domId,
                                  @RequestParam(value="ownerId",required = true) Integer ownerId,
                                  @RequestHeader(value = "Validate", required = true) String validate) {

        Integer varifycode  = domainMapper.varify(domId,validate);
        DomOwnerUser domOwnerUser = new DomOwnerUser();
        domOwnerUser.setDomId(domId);
        domOwnerUser.setOwnerId(ownerId);

        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能查询域"+domId+"下的属主"+ownerId+"拥有的成员树，因为"+
                            ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else if(!(domResOwnerService1.ownerExist(domId,ownerId))){
            throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(),
                    "查询失败，因为域"+domId+"下的属主"+ownerId+"不存在"
            );
        }else {
            return userService.queryUsers(domOwnerUser);
        }
    }






}

