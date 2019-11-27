package cn.itsmith.sysutils.resacl.controller;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.entities.*;
import cn.itsmith.sysutils.resacl.service.DomResOperationService;
import cn.itsmith.sysutils.resacl.service.DomResTypeService;
import cn.itsmith.sysutils.resacl.service.DomainService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/domResOperation")
@Api(tags = "资源可用权限的基础类api文档")
public class DomResOperationController {
    @Autowired
    DomResOperationService domResOperationService;
    @Autowired
    DomResTypeService domResTypeService;
    @Autowired
    DomainService domainService;

    //添加资源可用权限
    @ApiOperation(value = "添加资源可用权限",
            notes = "添加资源可用权限  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOperationA工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，根据域标识和资源种类标识判断该资源种类是否存在，存在则继续执行，不存在抛出错误；" +
                    "接着判断新添加的资源可用权限是否已经存在，若存在抛出异常，不存在则继续执行；" +
                    "成功添加后返回成功码，以及添加的资源可用权限自增主键。  \n" +
                    "成功码：  \n" +
                    "  200：成功为标识为XXX的域下标识为XXX的资源种类添加标识为XXX的资源可用权限  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2003：新增资源可用权限中域标识为XXX的域下标识为XXX的资源种类不存在,添加失败  \n" +
                    "   2010：已存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，添加失败  \n" +
                    "   8000：未知错误，为标识为XXX的域下标识为XXX的资源种类添加资源可用权限失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationA", value = "资源可用权限", required = true, dataType = "DomResOperationA", paramType = "body")
    })
    @PostMapping("/addDomResOperation")
    public ResultUtils addDomResOperation(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationA domResOperationA) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationA.getDomId(), Auth);
        //判断资源种类是否存在
        if(!domResTypeService.domResTypeExist(domResOperationA.getDomId(),
                domResOperationA.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("新增资源可用权限中域标识为%d的域下标识为%d的资源种类不存在,添加失败",
                            domResOperationA.getDomId(), domResOperationA.getResTypeId()));
        if(domResOperationService.resOperationExist(domResOperationA.getDomId(),
                domResOperationA.getResTypeId(), domResOperationA.getOpId()))
            throw new FailedException(ResponseInfo.OPEXIST_ERROR.getErrorCode(),
                    String.format("已存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限，添加失败",
                            domResOperationA.getDomId(), domResOperationA.getResTypeId(), domResOperationA.getOpId()));
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationA.getDomId());
        domResOperation.setResTypeId(domResOperationA.getResTypeId());
        domResOperation.setOpId(domResOperationA.getOpId());
        domResOperation.setOpDes(domResOperationA.getOpDes());
        domResOperation.setIsExtend(domResOperationA.getIsExtend());
        domResOperation.setIsCommon(domResOperationA.getIsCommon());
        return domResOperationService.addDomResOperation(domResOperation);
    }

    @ApiOperation(value = "删除资源可用权限",
            notes = "删除资源可用权限  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入domResOperationD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，根据域标识和资源种类标识判断该资源种类是否存在，存在则继续执行，不存在抛出错误；" +
                    "接着判断要删除的的资源可用权限是否已经存在，若不存在抛出异常，存在则继续执行；" +
                    "需要判断资源可用权限是否使用中（未实现）；" +
                    "成功删除后返回成功码，以及删除的资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功移除标识为XXX的域下标识为XXX的资源种类添加标识为XXX的资源可用权限  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2003：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，删除失败  \n" +
                    "   2006：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，删除失败  \n" +
                    "   8000：未知错误，移除标识为XXX的域下标识为XXX的资源种类的标识为XXX的资源可用权限失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationD", value = "资源可用权限", required = true, dataType = "DomResOperationD", paramType = "body")
    })
    @DeleteMapping("/deleteDomResOperation")
    public ResultUtils deleteDomResOwner(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationD domResOperationD){
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationD.getDomId(), Auth);
        //判断资源种类是否存在
        if(!domResTypeService.domResTypeExist(domResOperationD.getDomId(),
                domResOperationD.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("需要移除的资源可用权限中域标识为%d的域下标识为%d的资源种类不存在,移除失败",
                            domResOperationD.getDomId(), domResOperationD.getResTypeId()));
        //先查找是否存在该条记录
        if(!domResOperationService.resOperationExist(domResOperationD.getDomId(),
                domResOperationD.getResTypeId(), domResOperationD.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限，删除失败",
                            domResOperationD.getDomId(), domResOperationD.getResTypeId(), domResOperationD.getOpId()));
        //判断是否使用中
        domResOperationService.opUsing(domResOperationD.getDomId(), domResOperationD.getResTypeId(), domResOperationD.getOpId());
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationD.getDomId());
        domResOperation.setResTypeId(domResOperationD.getResTypeId());
        domResOperation.setOpId(domResOperationD.getOpId());
        return domResOperationService.deleteResOperation(domResOperation);
    }

    @ApiOperation(value = "查询资源可用权限",
            notes = "查询资源可用权限  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入域标识、资源类型标识，首先根据域标识和域令牌判断是否授权；" +
                    "其次，根据域标识和资源种类标识判断该资源种类是否存在，存在则继续执行，不存在抛出错误；" +
                    "接着根据域标识、资源种类标识查找到该资源种类的可用权限；" +
                    "成功删除后返回成功码，以及该资源种类的资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功移除标识为XXX的域下标识为XXX的资源种类添加标识为XXX的资源可用权限  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2003：需要查找的资源可用权限中域标识为%d的域下标识为%d的资源种类不存在,查找失败  \n" +
                    "   8000：未知错误，查找标识为XXX的域下标识为XXX的资源种类的资源可用权限失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domId", value = "域标识", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "resTypeId", value = "资源类型标识", required = true, dataType = "int", paramType = "query"),
    })
    @GetMapping("/getDomResOperation/{domId}")
    public ResultUtils getDomResOperation(@RequestHeader(value = "Auth", required = true) String Auth, @PathVariable(value = "domId") int domId, @RequestParam(value = "resTypeId") int resTypeId) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domId, Auth);
        //判断资源种类是否存在
        if(!domResTypeService.domResTypeExist(domId, resTypeId))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("需要查找的资源可用权限中域标识为%d的域下标识为%d的资源种类不存在,查找失败",
                            domId, resTypeId));

        return domResOperationService.getResOperation(domId, resTypeId);
    }

    @ApiOperation(value = "修改资源可用权限描述",
            notes = "修改资源可用权限描述  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOperationU工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，查找表中是否存在该条记录，存在则继续执行，不存在抛出异常错误；" +
                    "成功修改后返回成功码，以及修改的修改资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限的描述为XXX  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,更新失败  \n" +
                    "   2006：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，修改描述失败  \n" +
                    "  8000：未知错误，未能修改标识为XXX的域下标识为XXX的属主的描述  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationU", value = "域资源属主", required = true, dataType = "DomResOperationU", paramType = "body")
    })
    @PostMapping("/modifyOwnerDes")
    public ResultUtils modifyOwnerDes(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationU domResOperationU) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationU.getDomId(), Auth);
        if(!domResOperationService.resOperationExist(domResOperationU.getDomId(),
                domResOperationU.getResTypeId(), domResOperationU.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限，修改描述失败",
                            domResOperationU.getDomId(), domResOperationU.getResTypeId(), domResOperationU.getOpId()));
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationU.getDomId());
        domResOperation.setResTypeId(domResOperationU.getResTypeId());
        domResOperation.setOpId(domResOperationU.getOpId());
        domResOperation.setOpDes(domResOperationU.getOpDes());
        return domResOperationService.updateOpDes(domResOperation);
    }

    @ApiOperation(value = "修改资源可用权限为可继承",
            notes = "修改资源可用权限为可继承  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOperationD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，查找表中是否存在该条记录，存在则继续执行，不存在抛出异常错误；" +
                    "成功修改后返回成功码，以及修改的修改资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为可继承  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,更新失败  \n" +
                    "  2006：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限  \n" +
                    "  8000：未知错误，未能修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为可继承  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationD", value = "域资源属主", required = true, dataType = "DomResOperationD", paramType = "body")
    })
    @PostMapping("/setExtend")
    public ResultUtils setExtend(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationD domResOperationD) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationD.getDomId(), Auth);
        if(!domResOperationService.resOperationExist(domResOperationD.getDomId(),
                domResOperationD.getResTypeId(), domResOperationD.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
                            domResOperationD.getDomId(), domResOperationD.getResTypeId(), domResOperationD.getOpId()));
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationD.getDomId());
        domResOperation.setResTypeId(domResOperationD.getResTypeId());
        domResOperation.setOpId(domResOperationD.getOpId());
        return domResOperationService.setExtend(domResOperation);
    }

    @ApiOperation(value = "修改资源可用权限不为可继承",
            notes = "修改资源可用权限不为可继承  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOperationD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，查找表中是否存在该条记录，存在则继续执行，不存在抛出异常错误；" +
                    "成功修改后返回成功码，以及修改的修改资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为不可继承  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,更新失败  \n" +
                    "   2006：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，修改描述失败  \n" +
                    "  8000：未知错误，未能成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为不可继承  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationD", value = "域资源属主", required = true, dataType = "DomResOperationD", paramType = "body")
    })
    @PostMapping("/cancelExtend")
    public ResultUtils cancelExtend(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationD domResOperationD) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationD.getDomId(), Auth);
        if(!domResOperationService.resOperationExist(domResOperationD.getDomId(),
                domResOperationD.getResTypeId(), domResOperationD.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
                            domResOperationD.getDomId(), domResOperationD.getResTypeId(), domResOperationD.getOpId()));
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationD.getDomId());
        domResOperation.setResTypeId(domResOperationD.getResTypeId());
        domResOperation.setOpId(domResOperationD.getOpId());
        return domResOperationService.cancelExtend(domResOperation);
    }

    @ApiOperation(value = "修改资源可用权限为可通用",
            notes = "修改资源可用权限为可通用  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOperationD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，查找表中是否存在该条记录，存在则继续执行，不存在抛出异常错误；" +
                    "成功修改后返回成功码，以及修改的修改资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为可通用  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,更新失败  \n" +
                    "   2006：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，修改描述失败  \n" +
                    "  8000：未知错误，未能成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为可通用  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationD", value = "域资源属主", required = true, dataType = "DomResOperationD", paramType = "body")
    })
    @PostMapping("/setCommon")
    public ResultUtils setCommon(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationD domResOperationD) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationD.getDomId(), Auth);
        if(!domResOperationService.resOperationExist(domResOperationD.getDomId(),
                domResOperationD.getResTypeId(), domResOperationD.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
                            domResOperationD.getDomId(), domResOperationD.getResTypeId(), domResOperationD.getOpId()));
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationD.getDomId());
        domResOperation.setResTypeId(domResOperationD.getResTypeId());
        domResOperation.setOpId(domResOperationD.getOpId());
        return domResOperationService.setCommon(domResOperation);
    }

    @ApiOperation(value = "修改资源可用权限为不可通用",
            notes = "修改资源可用权限为不可通用  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOperationD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，查找表中是否存在该条记录，存在则继续执行，不存在抛出异常错误；" +
                    "成功修改后返回成功码，以及修改的修改资源可用权限。  \n" +
                    "成功码：  \n" +
                    "  200：成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为可通用  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,更新失败  \n" +
                    "  2006：不存在标识为XXX的域下的标识为XXX的资源种类的标识为XXX的资源可用权限，修改描述失败  \n" +
                    "  8000：未知错误，未能成功修改标识为XXX的域下标识为XXX的资源种类的标识为XXX的可用权限为可通用  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOperationD", value = "域资源属主", required = true, dataType = "DomResOperationD", paramType = "body")
    })
    @PostMapping("/cancelCommon")
    public ResultUtils cancelCommon(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResOperationD domResOperationD) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOperationD.getDomId(), Auth);
        if(!domResOperationService.resOperationExist(domResOperationD.getDomId(),
                domResOperationD.getResTypeId(), domResOperationD.getOpId()))
            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
                            domResOperationD.getDomId(), domResOperationD.getResTypeId(), domResOperationD.getOpId()));
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domResOperationD.getDomId());
        domResOperation.setResTypeId(domResOperationD.getResTypeId());
        domResOperation.setOpId(domResOperationD.getOpId());
        return domResOperationService.cancelCommon(domResOperation);
    }
}
