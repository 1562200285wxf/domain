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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "资源属主才做的基础类api文档")
public class DomResOwnerController {
    @Value("$checkToken")
    private String checkToken;
    @Autowired
    DomResOwnerService domResOwnerService;
    @Autowired
    DomOwnerUserService domOwnerUserService;
    @Autowired
    DomainService domainService;
    @Autowired
    DomResInstanceService domResInstanceService;
    @Autowired
    DomResTypeService domResTypeService;
    @Autowired
    DomOwnerResService domOwnerResService;
    @Autowired
    DomResOperationService domResOperationService;

    //添加域资源属主
    @ApiOperation(value = "添加域资源属主",
            notes = "添加域资源属主  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOwnerA工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，根据域标识和新添加的属主的pid判断其父属主是否存在，存在则继续执行，不存在抛出错误；" +
                    "成功添加后返回成功码，以及添加的资源属主的标识。  \n" +
                    "成功码：  \n" +
                    "  200：成功为标识为XXX的域添加标识为XXX的属主  \n" +
                    "错误码：  \n" +
                    "   1001：域或域令牌错误  \n" +
                    "   2008：为标识为XXX的域添加属主，该属主的标识为XXX的父属主不存在于标识为XXX的域中，添加失败  \n" +
                    "   8000：未知错误，为标识为XXX的域添加属主失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOwnerA", value = "域资源属主", required = true, dataType = "DomResOwnerA", paramType = "body")
    })
    @PostMapping("/owner")
    public ResultUtils addDomResOwner(@RequestHeader(value = "Auth", required = false) String Auth, @RequestHeader(value = "userId", required = false) Integer userId, @RequestBody DomResOwnerA domResOwnerA) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOwnerA.getDomId(), Auth);

        //根据父节点信息查找是否存在父节点的资源属主
        System.out.println(domResOwnerA);
        if (domResOwnerA.getPId() != 0) {
            if (!domResOwnerService.ownerExist(domResOwnerA.getDomId(),
                    domResOwnerA.getPId()))
                throw new FailedException(ResponseInfo.OWNERPID_ERROR.getErrorCode(),
                        String.format("为标识为%d的域添加属主，该属主的标识为%d的父属主不存在于标识为%d的域中，添加失败",
                                domResOwnerA.getDomId(), domResOwnerA.getPId(), domResOwnerA.getDomId()));
            if (!domOwnerUserService.isOwnerAdmin(domResOwnerA.getDomId(),
                    domResOwnerA.getPId(), userId)) {
                throw new FailedException(ResponseInfo.AUTH_ERROR.getErrorCode(),
                        String.format("域标识为%d的域下的标识为%d的属主不存在标识为%d的用户不是管理员",
                                domResOwnerA.getDomId(), domResOwnerA.getPId(), userId));
            }
        }
        DomResOwner domResOwner = new DomResOwner();
        domResOwner.setDomId(domResOwnerA.getDomId());
        domResOwner.setOwnerDes(domResOwnerA.getOwnerDes());
        domResOwner.setOwnerName(domResOwnerA.getOwnerName());
        domResOwner.setPId(domResOwnerA.getPId());
        return domResOwnerService.addDomResOwner(domResOwner);
    }

    //修改域资源属主描述
    @ApiOperation(value = "修改域资源属主描述",
            notes = "修改域资源属主描述  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOwnerU工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，先根据资源属主标识查找是否存在此资源属主，存在则继续执行，不存在抛出异常错误；" +
                    "成功修改后返回成功码，以及修改的资源属主的信息。  \n" +
                    "成功码：  \n" +
                    "  200：成功修改域标识为XXX域下的标识为XXX的属主描述为XXX  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,更新失败  \n" +
                    "  8000：未知错误，未能修改标识为XXX的域下标识为XXX的属主的描述  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOwnerU", value = "域资源属主", required = true, dataType = "DomResOwnerU", paramType = "body")
    })
    @PutMapping("/owner")
    public ResultUtils modifyOwner(@RequestHeader(value = "Auth", required = true) String Auth, @RequestHeader(value = "userId", required = false) Integer userId, @RequestBody DomResOwnerU domResOwnerU) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOwnerU.getDomId(), Auth);
        //先根据资源属主标识查找是否存在此资源属主
        if (!domResOwnerService.ownerExist(domResOwnerU.getDomId(),
                domResOwnerU.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下不存在标识为%d的属主,更新失败",
                            domResOwnerU.getDomId(), domResOwnerU.getOwnerId()));
        if (!domOwnerUserService.isOwnerAdmin(domResOwnerU.getDomId(),
                domResOwnerU.getOwnerId(), userId)) {
            throw new FailedException(ResponseInfo.AUTH_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下的标识为%d的属主下标识为%d的用户不是管理员",
                            domResOwnerU.getDomId(), domResOwnerU.getOwnerId(), userId));
        }
        DomResOwner domResOwner = new DomResOwner();
        domResOwner.setDomId(domResOwnerU.getDomId());
        domResOwner.setOwnerId(domResOwnerU.getOwnerId());
        domResOwner.setOwnerName(domResOwnerU.getOwnerName());
        domResOwner.setOwnerDes(domResOwnerU.getOwnerDes());
        return domResOwnerService.updateOwnerDes(domResOwner);
    }

    @ApiOperation(value = "删除域资源属主",
            notes = "删除域资源属主  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入DomResOwnerD工具类，首先根据域标识和域令牌判断是否授权；" +
                    "其次，先根据资源属主标识查找是否存在此资源属主，存在则继续执行，不存在抛出异常错误；" +
                    "接着判断此资源属主是否使用中：" +
                    "1.根据资源属主标识查找所有属主中有没有以该节点为父节点，没有，则继续执行，有则抛出异常；" +
                    "2.根据属主标识查找属主成员中有没有存在该属主的成员，没有，则继续执行，有则抛出异常；" +
                    "3.根据属主标识查找该属主是否拥有资源种类，没有，则继续执行，有则抛出异常。" +
                    "若以上三种情况都没有，则执行删除操作，将该资源属主移除。  \n" +
                    "成功码：  \n" +
                    "  200：成功删除标识为XXX域下标识为XXX的属主  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主，删除失败  \n" +
                    "  4000：域标识为%d的域下的标识为%d的属主还是标识为%s属主的父属主,不能删除  \n" +
                    "  4001：域标识为%d的域下的标识为%d的属主还存在标识为%s的成员,不能删除  \n" +
                    "  4002：域标识为%d的域下的标识为%d的属主还存在标识为%s的资源种类,不能删除  \n" +
                    "  8000：未知错误，未能修改标识为XXX的域下标识为XXX的属主的描述  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domResOwnerD", value = "域资源属主", required = true, dataType = "DomResOwnerD", paramType = "body")
    })
    @DeleteMapping("/owner")
    public ResultUtils deleteDomResOwner(@RequestHeader(value = "Auth", required = true) String Auth, @RequestHeader(value = "userId", required = false) Integer userId, @RequestBody DomResOwnerD domResOwnerD) {
        /**
         *
         */
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domResOwnerD.getDomId(), Auth);
        //先根据资源属主标识查找是否存在此资源属主
        if (!domResOwnerService.ownerExist(domResOwnerD.getDomId(),
                domResOwnerD.getOwnerId()))
            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下不存在标识为%d的属主,删除失败",
                            domResOwnerD.getDomId(), domResOwnerD.getOwnerId()));
        if (!domOwnerUserService.isOwnerAdmin(domResOwnerD.getDomId(),
                domResOwnerD.getOwnerId(), userId)) {
            throw new FailedException(ResponseInfo.AUTH_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下的标识为%d的属主不存在标识为%d的用户不是管理员",
                            domResOwnerD.getDomId(), domResOwnerD.getOwnerId(), userId));
        }
        //判断此属主是否使用中
        domResOwnerService.ownerUsing(domResOwnerD.getDomId(),
                domResOwnerD.getOwnerId());
        DomResOwner domResOwner = new DomResOwner();
        domResOwner.setDomId(domResOwnerD.getDomId());
        domResOwner.setOwnerId(domResOwnerD.getOwnerId());
        return domResOwnerService.deleteDomResOwner(domResOwner);
    }

    @ApiOperation(value = "获取域资源属主树",
            notes = "获取域资源属主树  \n" +
                    "逻辑：在请求头中传入域令牌，在请求体中传入域标识、资源属主标识，首先根据域标识和域令牌判断是否授权；" +
                    "其次，先根据资源属主标识查找是否存在此资源属主，存在则继续执行，不存在抛出异常错误；" +
                    "获取属主树：根据域标识、属主标识找到该属主，遍历所有属主，找到所有属主的pid等于传入的属主标识，将这些属主设为子节点；" +
                    "再递归遍历子节点，直到递归的所有属主没有子节点，返回属主树的头节点；" +
                    "成功修改后返回成功码，以及资源属主树。  \n" +
                    "成功码：  \n" +
                    "  200：成功获取标识为XXX的域下的标识为XXX的属主的属主树  \n" +
                    "错误码：  \n" +
                    "  1001：域或域令牌错误  \n" +
                    "  2002：域标识为XXX的域下不存在标识为XXX的属主,获取属主树失败  \n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Auth", value = "域令牌", required = false, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "domId", value = "域标识", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "ownerId", value = "资源属主标识", required = true, dataType = "int", paramType = "query"),
    })
    @GetMapping("/owner-tree/{domId}")
    public ResultUtils getDomResOwnerTree(@RequestHeader(value = "Auth", required = true) String Auth, @PathVariable(value = "domId") int domId, @RequestParam(value = "ownerId") int ownerId) {
        //根据令牌和domain判断请求请求是否正确
        domainService.verify(domId, Auth);
        //先根据资源属主标识查找是否存在此资源属主
        if (ownerId != 0)
            if (!domResOwnerService.ownerExist(domId, ownerId))
                throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                        String.format("域标识为%d的域下不存在标识为%d的属主,获取属主树失败", domId, ownerId));
        return domResOwnerService.getDomResOwnerTree(domId, ownerId);
    }

    /**
     * 根据操作的list查出不拥有这些操作的成员
     *
     * @param Auth
     * @return
     */
    @ApiOperation(value = "根据操作的list查出不拥有这些操作的属主", notes = "针对属主成员的查询操作")
    @PostMapping(value = "/operation-owners")
    public ResultUtils getOperationOwners(@RequestHeader(value = "Auth", required = true) String Auth, @RequestBody List<DomResOperationL> domResOperationLS) {
        for (DomResOperationL domResOperationL : domResOperationLS) {
            //根据令牌和domain判断请求请求是否正确
            domainService.verify(domResOperationL.getDomId(), Auth);
            //根据属主标识查找是否存在该属主
            if (!domResOwnerService.ownerExist(domResOperationL.getDomId(),
                    domResOperationL.getOwnerId()))
                throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
                        String.format("域标识为%d的域下不存在标识为%d的属主",
                                domResOperationL.getDomId(), domResOperationL.getOwnerId()));

            //根据资源种类标识查找是否存在该资源种类
            if (!domResTypeService.domResTypeExist(domResOperationL.getDomId(),
                    domResOperationL.getResTypeId()))
                throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
                        String.format("域标识为%d的域下标识为%d的资源种类不存在,添加失败",
                                domResOperationL.getDomId(), domResOperationL.getResTypeId()));

            //根据域id，属主id和资源类型id查找此属主是否拥有该资源种类
            if (!domOwnerResService.ownerResExist(domResOperationL.getDomId(),
                    domResOperationL.getOwnerId(), domResOperationL.getResTypeId()))
                throw new FailedException(ResponseInfo.GETOWNERRES_ERROR.getErrorCode(),
                        String.format("添加的实例中域标识为%d的域下标识为%d的资源属主不存在标识为%d的资源种类,添加失败",
                                domResOperationL.getDomId(), domResOperationL.getOwnerId(), domResOperationL.getResTypeId()));
            if (!domResOperationService.resOperationExist(domResOperationL.getDomId(),
                    domResOperationL.getResTypeId(), domResOperationL.getOpId()))
                throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
                        String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
                                domResOperationL.getDomId(), domResOperationL.getResTypeId(), domResOperationL.getOpId()));
            //判断是否存在实例
            if (!domResInstanceService.resInstanceExist(domResOperationL.getDomId(), domResOperationL.getOwnerId(),
                    domResOperationL.getResTypeId(), domResOperationL.getResId()))
                throw new FailedException(ResponseInfo.GETRESINTANCE_ERROR.getErrorCode(),
                        String.format("不存在域标识为%d的域下标识为%d的资源属主中标识为%d的资源种类的标识为%d的实例",
                                domResOperationL.getDomId(), domResOperationL.getOwnerId(),
                                domResOperationL.getResTypeId(), domResOperationL.getResId()));
        }
        return domResOwnerService.getOperationOwners(domResOperationLS);

    }

}
