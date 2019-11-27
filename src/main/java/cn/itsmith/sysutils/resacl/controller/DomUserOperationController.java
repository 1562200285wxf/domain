package cn.itsmith.sysutils.resacl.controller;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomainMapper;
import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import cn.itsmith.sysutils.resacl.entities.DomUserOperationA;
import cn.itsmith.sysutils.resacl.service.*;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value="授权Controller",tags={"资源授权Api"})
@RestController

public class DomUserOperationController {

    @Autowired
    DomUserOperationService domUserOperationService;
    @Autowired
    DomainService domainService;
    @Autowired
    DomResInstanceService domResInstanceService;
    @Autowired
    DomResOwnerService domResOwnerService;
    @Autowired
    DomResTypeService domResTypeService;
    @Autowired
    DomOwnerResService domOwnerResService;
    @Autowired
    DomResOperationService domResOperationService;
    @Autowired
    DomOwnerUserService domOwnerUserService;
    @ApiOperation(value = "资源授权的单纯查询", notes = "针对资源授权查询操作  \n" +
            "查询成功代码：200  \n" +
            "查询成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.该成员没有资源授权记录【对应异常代码：3001】  \n" +
            "...")
    @RequestMapping(value="/queryuseroperation",method = RequestMethod.GET)
    public ResultUtils queryUserOperation(@RequestParam(value="domId",required = true) Integer domId,
                                          @RequestParam(value="resTypeId",required = true) Integer resTypeId,
                                          @RequestParam(value="resId",required = true) Integer resId,
                                          @RequestParam(value="ownerId",required = true) Integer ownerId,
                                          @RequestParam(value="userId",required = true) Integer userId,
                                          @RequestHeader(value = "token", required = true) String validate){
        DomUserOperation domUserOperation = new DomUserOperation();
        domUserOperation.setDomId(domId);
        domUserOperation.setResId(resId);
        domUserOperation.setResTypeId(resTypeId);
        domUserOperation.setOwnerId(ownerId);
        domUserOperation.setUserOwnerId(userId);
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domId, validate);
            System.out.print(domUserOperationService.selectOps(domUserOperation));
            return domUserOperationService.selectOps(domUserOperation);

    }

    @ApiOperation(value = "资源授权的添加", notes = "针对资源授权的添加操作  \n" +
            "添加成功代码：200  \n" +
            "添加成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.已经存在这条资源授权记录，不能重复添加【对应异常代码 3002】  \n" +
            "...")
    @RequestMapping(value="/adduseroperation",method = RequestMethod.POST)
    public ResultUtils addUserOperation(@RequestBody DomUserOperationA domUserOperationA, @RequestHeader(value = "token", required = true) String validate){

        domainService.verify(domUserOperationA.getDomId(), validate);

        //根据属主标识查找是否存在该属主
        if(!domResOwnerService.ownerExist(domUserOperationA.getDomId(),
                domUserOperationA.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("添加的资源授权中域标识为%d的域下不存在标识为%d的属主,添加失败",
                            domUserOperationA.getDomId(), domUserOperationA.getOwnerId()));
        //根据资源种类标识查找是否存在该资源种类
        if(!domResTypeService.domResTypeExist(domUserOperationA.getDomId(),
                domUserOperationA.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("添加的资源授权中中域标识为%d的域下标识为%d的资源种类不存在,添加失败",
                            domUserOperationA.getDomId(), domUserOperationA.getResTypeId()));
        //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
        if(!domOwnerResService.ownerResExist(domUserOperationA.getDomId(),
                domUserOperationA.getOwnerId(), domUserOperationA.getResTypeId()))
            throw new FailedException(ResponseInfo.GETOWNERRES_ERROR.getErrorCode(),
                    String.format("添加的资源授权中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类,添加失败",
                            domUserOperationA.getOwnerId(), domUserOperationA.getResTypeId()));
        //根据域id，资源种类id，和opid判断是否有此资源可用权限
        if(!domResOperationService.resOperationExist(domUserOperationA.getDomId(),
                domUserOperationA.getResTypeId(), domUserOperationA.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("添加的资源授权中不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
                            domUserOperationA.getDomId(), domUserOperationA.getResTypeId(), domUserOperationA.getResId()));
        //根据域id，属主id，资源类型id，以及实例id查询是否存在该资源实例
        if(!domResInstanceService.resInstanceExist(domUserOperationA.getDomId(), domUserOperationA.getOwnerId(),
                domUserOperationA.getResTypeId(), domUserOperationA.getResId()))
            throw new FailedException(ResponseInfo.GETRESINTANCE_ERROR.getErrorCode(),
                    String.format("不存在域标识为%d的域下标识为%d的资源属主中标识为%d的资源种类的标识为%d的实例,删除失败",
                            domUserOperationA.getDomId(), domUserOperationA.getOwnerId(),
                            domUserOperationA.getResTypeId(), domUserOperationA.getResId()));
        //根据types值判断是人员还是属主，对其进行存在的判断
        if(domUserOperationA.getTypes() == 1){
            //根据属主标识查找是否存在该属主
            if(!domResOwnerService.ownerExist(domUserOperationA.getDomId(),
                    domUserOperationA.getOwnerId()))
                throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                        String.format("添加的资源授权中域标识为%d的域下不存在标识为%d的属主,删除失败",
                                domUserOperationA.getDomId(), domUserOperationA.getOwnerId()));
        }else if(domUserOperationA.getTypes() == 0){
            //判断授权的用户是否存在
            if(!domOwnerUserService.userExist(domUserOperationA.getDomId(),
                    domUserOperationA.getOwnerId(), domUserOperationA.getUserOwnerId()))
                        throw new FailedException(ResponseInfo.GETUSER_ERROR.getErrorCode(),
                                String.format("添加的资源授权中域标识为%d的域下的标识为%d的属主不存在标识为%d的用户",
                                        domUserOperationA.getDomId(), domUserOperationA.getOwnerId(), domUserOperationA.getUserOwnerId()));

        }
        DomUserOperation domUserOperation = new DomUserOperation();
        //7个
        domUserOperation.setDomId(domUserOperationA.getDomId());
        domUserOperation.setResId(domUserOperationA.getResId());
        domUserOperation.setTypes(domUserOperationA.getTypes());
        domUserOperation.setOpId(domUserOperationA.getOpId());
        domUserOperation.setResTypeId(domUserOperationA.getResTypeId());
        domUserOperation.setOwnerId(domUserOperationA.getOwnerId());
        domUserOperation.setUserOwnerId(domUserOperationA.getUserOwnerId());
        return domUserOperationService.addOp(domUserOperation);

    }

    @ApiOperation(value = "资源授权的删除", notes = "针对资源授权的删除操作  \n" +
            "删除成功代码：200  \n" +
            "删除成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.不存在这条资源授权记录，不能删除【对应异常代码：3003】  \n" +
            "...")
    @RequestMapping(value="/removeuseroperation",method = RequestMethod.POST)
    public ResultUtils delUserOperation(@RequestBody DomUserOperationA domUserOperationA, @RequestHeader(value = "token", required = true) String validate){
        domainService.verify(domUserOperationA.getDomId(), validate);

        DomUserOperation domUserOperation = new DomUserOperation();
        //7个跟添加操作一样
        domUserOperation.setDomId(domUserOperationA.getDomId());
        domUserOperation.setResId(domUserOperationA.getResId());
        domUserOperation.setTypes(domUserOperationA.getTypes());
        domUserOperation.setOpId(domUserOperationA.getOpId());
        domUserOperation.setResTypeId(domUserOperationA.getResTypeId());
        domUserOperation.setOwnerId(domUserOperationA.getOwnerId());
        domUserOperation.setUserOwnerId(domUserOperationA.getUserOwnerId());
        return domUserOperationService.delOp(domUserOperation);
    }

}
