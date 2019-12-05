package cn.itsmith.sysutils.resacl.controller;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.entities.*;
import cn.itsmith.sysutils.resacl.service.*;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "资源实例的基础类api文档")
public class DomResInstanceController {

    @Autowired
    DomResInstanceService domResInstanceService;
    @Autowired
    DomResOwnerService domResOwnerService;
    @Autowired
    DomResTypeService domResTypeService;
    @Autowired
    DomOwnerResService domOwnerResService;
    @Autowired
    DomainService domainService;

    //添加属主资源实例

    @ApiOperation(value = "添加属主资源实例",
            notes = "添加属主资源实例  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResInstanceA工具类，首先根据域标识和域令牌判断是否授权；" +
                    "根据属主标识查找是否存在该属主,存在则继续执行，不存在抛出错误；" +
                    "其次，根据域标识和资源种类标识判断该资源种类是否存在，存在则继续执行，不存在抛出错误；" +
                    "根据资源种类标识查找是否存在该资源种类,存在继续执行，不存在抛出异常；" +
                    "根据域id，属主id和资源类型id查找此属主是否拥有该资源种类，存在继续执行，不存在抛出异常；" +
                    "接着判断新添加的属主资源实例是否已经存在，若存在抛出异常，不存在则继续执行；" +
                    "成功添加后返回成功码，以及添加的属主资源实例自增主键。  \n" +
                    "成功码：  \n" +
                    "  200：成功为标识为XXX的域下标识为XXX的属主中标识为XXX的资源种类添加标识为XXX的资源实例  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2002：添加的实例中域标识为XXX的域下不存在标识为XXX的属主,添加失败  \n" +
                    "   2003：添加的实例中域标识为XXX的域下标识为XXX的资源种类不存在,添加失败  \n" +
                    "   2004：添加的实例中域标识为XXX的域下标识为XXX的资源属主不存在标识为XXX的资源种类,添加失败  \n" +
                    "   2009：已存在域标识为XXX的域下标识为XXX的资源属主中标识为XXX的资源种类的标识为XXX的实例,添加失败" +
                    "的域下标识为XXX的资源属主中标识为XXX的资源种类的标识为XXX的实例,添加失败  \n" +
                    "   8000：未知错误，数据插入失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResInstanceA", value = "属主资源实例", required = true, dataType = "DomResInstanceA", paramType = "body")
    })
    @PostMapping("/instance")
    public ResultUtils addDomResInstance(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResInstanceA domResInstanceA) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResInstanceA.getDomId(), Auth);
        //根据属主标识查找是否存在该属主
        if(!domResOwnerService.ownerExist(domResInstanceA.getDomId(),
                domResInstanceA.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("添加的实例中域标识为%d的域下不存在标识为%d的属主,添加失败",
                            domResInstanceA.getDomId(), domResInstanceA.getOwnerId()));

        //根据资源种类标识查找是否存在该资源种类
        if(!domResTypeService.domResTypeExist(domResInstanceA.getDomId(),
                domResInstanceA.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("添加的实例中域标识为%d的域下标识为%d的资源种类不存在,添加失败",
                            domResInstanceA.getDomId(), domResInstanceA.getResTypeId()));

        //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
        if(!domOwnerResService.ownerResExist(domResInstanceA.getDomId(),
                domResInstanceA.getOwnerId(), domResInstanceA.getResTypeId()))
            throw new FailedException(ResponseInfo.GETOWNERRES_ERROR.getErrorCode(),
                    String.format("添加的实例中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类,添加失败",
                            domResInstanceA.getDomId(), domResInstanceA.getOwnerId(), domResInstanceA.getResTypeId()));

        //判断是否已经存在该条记录
        if(domResInstanceService.resInstanceExist(domResInstanceA.getDomId(), domResInstanceA.getOwnerId(),
                domResInstanceA.getResTypeId(), domResInstanceA.getResId()))
            throw new FailedException(ResponseInfo.RESEXIST_ERROR.getErrorCode(),
                    String.format("已存在域标识为%d的域下标识为%d的资源属主中标识为%d的资源种类的标识为%d的实例,添加失败",
                            domResInstanceA.getDomId(), domResInstanceA.getOwnerId(),
                            domResInstanceA.getResTypeId(), domResInstanceA.getResId()));
        DomResInstance domResInstance = new DomResInstance();
        domResInstance.setDomId(domResInstanceA.getDomId());
        domResInstance.setOwnerId(domResInstanceA.getOwnerId());
        domResInstance.setResTypeId(domResInstanceA.getResTypeId());
        domResInstance.setResId(domResInstanceA.getResId());
        domResInstance.setResPid(domResInstanceA.getResPid());
//        domResInstance.setInsName(domResInstanceA.getInsName());
        return domResInstanceService.addDomResInstance(domResInstance);
    }

    @ApiOperation(value = "批量添加属主资源实例",
            notes = "批量添加属主资源实例  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResInstanceA工具类，首先根据域标识和域令牌判断是否授权；" +
                    "根据属主标识查找是否存在该属主,存在则继续执行，不存在抛出错误；" +
                    "其次，根据域标识和资源种类标识判断该资源种类是否存在，存在则继续执行，不存在抛出错误；" +
                    "根据资源种类标识查找是否存在该资源种类,存在继续执行，不存在抛出异常；" +
                    "根据域id，属主id和资源类型id查找此属主是否拥有该资源种类，存在继续执行，不存在抛出异常；" +
                    "接着判断新添加的属主资源实例是否已经存在，若存在抛出异常，不存在则继续执行；" +
                    "成功添加后返回成功码，以及添加的属主资源实例自增主键。  \n" +
                    "成功码：  \n" +
                    "  200：成功为标识为XXX的域下标识为XXX的属主中标识为XXX的资源种类添加标识为XXX的资源实例  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2002：添加的实例中域标识为XXX的域下不存在标识为XXX的属主,添加失败  \n" +
                    "   2003：添加的实例中域标识为XXX的域下标识为XXX的资源种类不存在,添加失败  \n" +
                    "   2004：添加的实例中域标识为XXX的域下标识为XXX的资源属主不存在标识为XXX的资源种类,添加失败  \n" +
                    "   2009：已存在域标识为XXX的域下标识为XXX的资源属主中标识为XXX的资源种类的标识为XXX的实例,添加失败" +
                    "的域下标识为XXX的资源属主中标识为XXX的资源种类的标识为XXX的实例,添加失败  \n" +
                    "   8000：未知错误，数据插入失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
//            @ApiImplicitParam(name = "domResInstanceAS", value = "属主资源实例", required = true,  paramType = "body")
    })
    @PostMapping("/instances")
    public ResultUtils addDomResInstances(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody List<DomResInstanceA> domResInstanceAS) {
        List<DomResInstance> domResInstances = new ArrayList<DomResInstance>();
        for(DomResInstanceA domResInstanceA : domResInstanceAS){
            //根据令牌和domain判断请求请求是否正确
            domainService.verify(domResInstanceA.getDomId(), Auth);
            //根据属主标识查找是否存在该属主
            if(!domResOwnerService.ownerExist(domResInstanceA.getDomId(),
                    domResInstanceA.getOwnerId()))
                throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                        String.format("添加的实例中域标识为%d的域下不存在标识为%d的属主,添加失败",
                                domResInstanceA.getDomId(), domResInstanceA.getOwnerId()));

            //根据资源种类标识查找是否存在该资源种类
            if(!domResTypeService.domResTypeExist(domResInstanceA.getDomId(),
                    domResInstanceA.getResTypeId()))
                throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                        String.format("添加的实例中域标识为%d的域下标识为%d的资源种类不存在,添加失败",
                                domResInstanceA.getDomId(), domResInstanceA.getResTypeId()));

            //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
            if(!domOwnerResService.ownerResExist(domResInstanceA.getDomId(),
                    domResInstanceA.getOwnerId(), domResInstanceA.getResTypeId()))
                throw new FailedException(ResponseInfo.GETOWNERRES_ERROR.getErrorCode(),
                        String.format("添加的实例中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类,添加失败",
                                domResInstanceA.getDomId(), domResInstanceA.getOwnerId(), domResInstanceA.getResTypeId()));

            //判断是否已经存在该条记录
            if(domResInstanceService.resInstanceExist(domResInstanceA.getDomId(), domResInstanceA.getOwnerId(),
                    domResInstanceA.getResTypeId(), domResInstanceA.getResId()))
                throw new FailedException(ResponseInfo.RESEXIST_ERROR.getErrorCode(),
                        String.format("已存在域标识为%d的域下标识为%d的资源属主中标识为%d的资源种类的标识为%d的实例,添加失败",
                                domResInstanceA.getDomId(), domResInstanceA.getOwnerId(),
                                domResInstanceA.getResTypeId(), domResInstanceA.getResId()));
            DomResInstance domResInstance = new DomResInstance();
            domResInstance.setDomId(domResInstanceA.getDomId());
            domResInstance.setOwnerId(domResInstanceA.getOwnerId());
            domResInstance.setResTypeId(domResInstanceA.getResTypeId());
            domResInstance.setResId(domResInstanceA.getResId());
            domResInstance.setResPid(domResInstanceA.getResPid());
            domResInstances.add(domResInstance);
        }
        return domResInstanceService.addDomResInstances(domResInstances);
    }
    //删除属主资源实例
    @ApiOperation(value = "删除属主资源实例",
            notes = "删除属主资源实例  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入domResInstanceD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "根据属主标识查找是否存在该属主,存在则继续执行，不存在抛出错误；" +
                    "其次，根据域标识和资源种类标识判断该资源种类是否存在，存在则继续执行，不存在抛出错误；" +
                    "根据资源种类标识查找是否存在该资源种类,存在继续执行，不存在抛出异常；" +
                    "根据域id，属主id和资源类型id查找此属主是否拥有该资源种类，存在继续执行，不存在抛出异常；" +
                    "是否需要判断该资源实例是否为其他实例的父实例（未确定，未实现）；" +
                    "接着判断要删除的属主资源实例是否已经存在，不存在抛出异常，存在则继续执行；" +
                    "删除的同时删除该资源实例的资源授权；" +
                    "成功添加后返回成功码，以及删除的属主资源实例。  \n" +
                    "成功码：  \n" +
                    "  200：成功删除标识为XXX的域下标识为XXX的属主中标识为XXX的资源种类中标识为XXX的资源实例  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2002：添加的实例中域标识为XXX的域下不存在标识为XXX的属主,添加失败  \n" +
                    "   2003：添加的实例中域标识为XXX的域下标识为XXX的资源种类不存在,添加失败  \n" +
                    "   2004：添加的实例中域标识为XXX的域下标识为XXX的资源属主不存在标识为%d的资源种类,添加失败  \n" +
                    "   2009：不存在域标识为XXX的域下标识为XXX的资源属主中标识为XXX的资源种类的标识为XXX的实例,添加失败  \n" +
                    "   8000：未知错误，删除失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResInstanceD", value = "属主资源实例", required = true, dataType = "DomResInstanceD", paramType = "body")
    })
    @DeleteMapping("/instance")
    public ResultUtils removeDomResInstance
     (@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResInstanceD domResInstanceD) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResInstanceD.getDomId(), Auth);
        //根据属主标识查找是否存在该属主
        if(!domResOwnerService.ownerExist(domResInstanceD.getDomId(),
                domResInstanceD.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("添加的实例中域标识为%d的域下不存在标识为%d的属主,删除失败",
                            domResInstanceD.getDomId(), domResInstanceD.getOwnerId()));

        //根据资源种类标识查找是否存在该资源种类
        if(!domResTypeService.domResTypeExist(domResInstanceD.getDomId(),
                domResInstanceD.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("添加的实例中域标识为%d的域下标识为%d的资源种类不存在,删除失败",
                            domResInstanceD.getDomId(), domResInstanceD.getResTypeId()));

        //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
        if(!domOwnerResService.ownerResExist(domResInstanceD.getDomId(),
                domResInstanceD.getOwnerId(), domResInstanceD.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("删除的实例中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类,删除失败",
                            domResInstanceD.getDomId(), domResInstanceD.getOwnerId(), domResInstanceD.getResTypeId()));

        //判断是否已经存在该条记录
        if(!domResInstanceService.resInstanceExist(domResInstanceD.getDomId(), domResInstanceD.getOwnerId(),
                domResInstanceD.getResTypeId(), domResInstanceD.getResId()))
            throw new FailedException(ResponseInfo.GETRESINTANCE_ERROR.getErrorCode(),
                    String.format("不存在域标识为%d的域下标识为%d的资源属主中标识为%d的资源种类的标识为%d的实例,删除失败",
                            domResInstanceD.getDomId(), domResInstanceD.getOwnerId(),
                            domResInstanceD.getResTypeId(), domResInstanceD.getResId()));

        DomResInstance domResInstance = new DomResInstance();
        domResInstance.setDomId(domResInstanceD.getDomId());
        domResInstance.setOwnerId(domResInstanceD.getOwnerId());
        domResInstance.setResTypeId(domResInstanceD.getResTypeId());
        domResInstance.setResId(domResInstanceD.getResId());
        return domResInstanceService.deleteDomResInstance(domResInstance);
    }

    @ApiOperation(value = "获取属主资源实例",
            notes = "获取属主资源实例  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入域标识、资源属主标识，首先根据域标识和域令牌判断是否授权；" +
                    "根据属主标识查找是否存在该属主,存在则继续执行，不存在抛出错误；" +
                    "查找实例：通过域标识和属主标识查找到该属主，通过查找属主树上的所有属主，将属主树上所有属主的资源实例加到需要查找的属主中" +
                    "成功添加后返回成功码，以及获取的属主资源实例。  \n" +
                    "成功码：  \n" +
                    "  200：成功获取标识为XXX的域下标识为XXX的属主的资源实例  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2002：添加的实例中域标识为XXX的域下不存在标识为XXX的属主,添加失败  \n" +
                    "   8000：未知错误，查找失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domId", value = "域标识", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "ownerId", value = "资源属主标识", required = true, dataType = "int", paramType = "query"),
    })
    @GetMapping("/instance/{domId}")
    public ResultUtils getOwnerResInstance(@RequestHeader(value = "Auth", required = true) String Auth, @PathVariable(value = "domId") int domId, @RequestParam(value = "ownerId") int ownerId) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domId, Auth);
        //根据属主标识查找是否存在该属主
        if(!domResOwnerService.ownerExist(domId, ownerId))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("不存在域标识为%d的域下不存在标识为%d的属主,查找失败",
                            domId, ownerId));

        return domResInstanceService.getOwnerInstance(domId, ownerId);
    }

    @ApiOperation(value = "获取属主资源实例",
            notes = "获取属主资源实例  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入域标识、资源属主标识，资源种类标识，首先根据域标识和域令牌判断是否授权；" +
                    "根据属主标识查找是否存在该属主,存在则继续执行，不存在抛出错误；" +
                    "查找实例：通过域标识和属主标识查找到该属主，通过查找属主树上的所有属主，将属主树上所有属主的资源实例加到需要查找的属主中" +
                    "成功添加后返回成功码，以及获取的属主资源实例。  \n" +
                    "成功码：  \n" +
                    "  200：成功获取标识为XXX的域下标识为XXX的属主的资源实例  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2002：添加的实例中域标识为XXX的域下不存在标识为XXX的属主,添加失败  \n" +
                    "   8000：未知错误，查找失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResInstanceG", value = "属主资源实例", required = true, dataType = "DomResInstanceG", paramType = "body")
    })
    @PostMapping("/restype-instance")
    public ResultUtils getOwnerRTypeInstance(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResInstanceG domResInstanceG) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResInstanceG.getDomId(), Auth);
        //根据属主标识查找是否存在该属主
        if(!domResOwnerService.ownerExist(domResInstanceG.getDomId(), domResInstanceG.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("不存在域标识为%d的域下不存在标识为%d的属主,查找失败",
                            domResInstanceG.getDomId(), domResInstanceG.getOwnerId()));
//根据资源种类标识查找是否存在该资源种类
        if(!domResTypeService.domResTypeExist(domResInstanceG.getDomId(),
                domResInstanceG.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("查找的实例中域标识为%d的域下标识为%d的资源种类不存在",
                            domResInstanceG.getDomId(), domResInstanceG.getResTypeId()));

        //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
        if(!domOwnerResService.ownerResExist(domResInstanceG.getDomId(),
                domResInstanceG.getOwnerId(), domResInstanceG.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("查找的实例中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类",
                            domResInstanceG.getDomId(), domResInstanceG.getOwnerId(), domResInstanceG.getResTypeId()));
        return domResInstanceService.getOwnerRTypeInstance(domResInstanceG.getDomId(), domResInstanceG.getOwnerId(), domResInstanceG.getResTypeId());
    }

    @ApiOperation(value = "获取特定种类的基础资源实例",
            notes = "获取特定种类的基础资源实例  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入域标识、资源属主标识，资源种类标识，首先根据域标识和域令牌判断是否授权；" +
                    "根据属主标识查找是否存在该属主,存在则继续执行，不存在抛出错误；" +
                    "查找实例：通过资源类型id到特定的资源表里面查找到，再把改属主已经拥有的剔除。  \n" +
                    "成功码：  \n" +
                    "  200：成功获取标识为XXX的域下标识为XXX的属主的资源实例  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2002：添加的实例中域标识为XXX的域下不存在标识为XXX的属主,添加失败  \n" +
                    "   8000：未知错误，查找失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResInstanceG", value = "属主资源实例", required = true, dataType = "DomResInstanceG", paramType = "body")
    })
    @PostMapping("/base-type-instance")
    public ResultUtils getBaseRTypeInstance(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody DomResInstanceG domResInstanceG) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResInstanceG.getDomId(), Auth);
        //根据属主标识查找是否存在该属主
        if(!domResOwnerService.ownerExist(domResInstanceG.getDomId(), domResInstanceG.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("不存在域标识为%d的域下不存在标识为%d的属主,查找失败",
                            domResInstanceG.getDomId(), domResInstanceG.getOwnerId()));
//根据资源种类标识查找是否存在该资源种类
        if(!domResTypeService.domResTypeExist(domResInstanceG.getDomId(),
                domResInstanceG.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("查找的实例中域标识为%d的域下标识为%d的资源种类不存在",
                            domResInstanceG.getDomId(), domResInstanceG.getResTypeId()));

        //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
        if(!domOwnerResService.ownerResExist(domResInstanceG.getDomId(),
                domResInstanceG.getOwnerId(), domResInstanceG.getResTypeId()))
            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                    String.format("查找的实例中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类",
                            domResInstanceG.getDomId(), domResInstanceG.getOwnerId(), domResInstanceG.getResTypeId()));
        return domResInstanceService.getBaseRTypeInstance(domResInstanceG.getDomId(), domResInstanceG.getOwnerId(), domResInstanceG.getResTypeId());
    }
}
