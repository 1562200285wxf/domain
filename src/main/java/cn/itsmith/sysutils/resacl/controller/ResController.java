package cn.itsmith.sysutils.resacl.controller;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.dao.DomainMapper;
import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.entities.DomOwnerResA;
import cn.itsmith.sysutils.resacl.service.DomOwnerResMaxService;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.service.ResService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value="资源Controller",tags={"属主拥有资源Api"})
@RestController
public class ResController {
    @Autowired
    DomResTypeMapper rTypeMapper;
    @Autowired
    DomainMapper domainMapper;
    @Autowired
    DomResOwnerMapper ownerMapper;
@Autowired
ResService resService;
    @Autowired
    DomResOwnerService domResOwnerService1;




    @ApiOperation(value = "属主拥有资源的添加", notes = "针对属主拥有资源的添加操作  \n" +
            "添加成功代码：200  \n" +
            "添加成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.目标插入到的属主一定要存在【对应异常代码：1002】  \n" +
            "3.不能重复添加一摸一样的一条属主拥有资源记录【对应异常代码：2001】  \n" +
            "4.资源种类基本表中一定要有目标资源才能向逻辑表中添加【对应异常代码：2002】  \n")
    @RequestMapping(value="/addownerres",method = RequestMethod.POST)
    public ResultUtils addOwnerRes(@RequestBody DomOwnerResA domOwnerResA, @RequestHeader(value = "token", required = true) String validate){
        DomOwnerRes domOwnerRes = new DomOwnerRes();
        domOwnerRes.setDomId(domOwnerResA.getDomId());
        domOwnerRes.setOwnerId(domOwnerResA.getOwnerId());
        domOwnerRes.setResTypeId(domOwnerResA.getResTypeId());

        Integer varifycode  = domainMapper.varify(domOwnerResA.getDomId(),validate);
        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能将资源种类"+domOwnerResA.getResTypeId()+"加入到域"+domOwnerResA.getDomId()+
                            "下，因为"+ ResponseInfo.AUTH_FAILED.getErrorMsg());
        }
        else{
            return  resService.addRes(domOwnerRes);
    }

    }
//    @Autowired
//    ResInstanceMapper resInstanceMapper;
    //删除
    @ApiOperation(value = "属主拥有的资源删除", notes = "针对属主拥有资源品种的删除操作  \n" +
            "删除成功代码：200  \n" +
            "删除成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.资源必须存在逻辑表`t_dom_owner_res`才能删除【对应异常代码：2004】  \n" +
            "3.资源必须没有被用作实例才能删除【对应异常代码：2006】  \n" +
            "4.属主不存在 【对应异常代码：3333】"
    )
    @RequestMapping(value="/removeownerRes",method = RequestMethod.POST)
    public ResultUtils removeOwnerRes(@RequestBody DomOwnerResA domOwnerResA, @RequestHeader(value = "Validate", required = true) String validate){

        DomOwnerRes domOwnerRes = new DomOwnerRes();
        domOwnerRes.setDomId(domOwnerResA.getDomId());
        domOwnerRes.setOwnerId(domOwnerResA.getOwnerId());
        domOwnerRes.setResTypeId(domOwnerResA.getResTypeId());
        Integer varifycode  = domainMapper.varify(domOwnerRes.getDomId(),validate);

        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能删除属主拥有的资源类型"+domOwnerResA.getResTypeId()+
                            "，因为"+ ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else if(!(domResOwnerService1.ownerExist(domOwnerRes.getDomId(),domOwnerRes.getOwnerId()))){
            throw new FailedException(3333,
                    "不能删除资源种类"+domOwnerRes.getResTypeId()+
                            "因为域"+domOwnerRes.getDomId()+"下没有属主"+domOwnerRes.getOwnerId());
        } else{
            return  resService.delRes(domOwnerRes);
        }

    }


//    //查询自此结点之下种类树
//    @Autowired
//    DomOwnerResServiceImpl domOwnerResService;
//    @ApiOperation(value = "查询资源种类树", notes = "针对属主拥有的资源的操作")
//    @RequestMapping(value="/queryrestree",method = RequestMethod.GET)
//    public ResultUtils queryResTree(@RequestParam(value="domId",required = true) Integer domId,
//                                     @RequestParam(value="ownerId",required = true) Integer ownerId,
//                                     @RequestParam(value="resTypeId",required = true) Integer resTypeId,
//                                     @RequestHeader(value = "Validate", required = true) String validate){
//        //验证varify
//        //属主是否存在
//        //res是否存在
//        //其中属主是否存在查的是基本表（属主树），res是否存在查的是逻辑表（种类树）
//        Integer varifycode  = domainMapper.varify(domId,validate);
//        List<Integer> listOwnerIds = ownerMapper.queryAllOwnerId(domId);
//        DomOwnerRes domOwnerRes1 = rTypeMapper.queryOwnerRes(domId, ownerId,resTypeId);
//        boolean hasOwner = false;
//
//        for (Integer id : listOwnerIds) {
//            if(ownerId==id){
//                hasOwner=true;
//                break;
//            }
//        }
//        if(varifycode!=1){
//            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(), ResponseInfo.AUTH_FAILED.getErrorMsg());
//        }else if(!hasOwner){
//            throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(), ResponseInfo.OWNER_FAILED.getErrorMsg());
//        }else if(domOwnerRes1==null){
//            throw new FailedException(ResponseInfo.NONRES_ERROR2.getErrorCode(), ResponseInfo.NONRES_ERROR2.getErrorMsg());
//        }else{
//            return domOwnerResService.getOwnerResTree(domId,ownerId);
//        }
//
//    }

    //查询自此属主的max种类
    /**
     * 1.没有该属主
     * 2.有该属主，找该属主下的pid为最小的资源s,
     *              如果pid的最小号都是0比如pid=0*3则直接全部返回
     *              如果pid的最小号出了0还有其他比如pid=1,2则根据从基本表中查找出的此pid查询逻辑表，如果没有结果则说明
     *              pid=0，1,2的资源就是三个最上级了
     */
    @Autowired
    DomOwnerResMaxService domOwnerResMaxService;
    //2.属主若没有拥有任何资源【对应异常代码：2007成员树不需要】
    @ApiOperation(value = "查询资源种类树max", notes = "针对属主拥有的资源的查询操作  \n" +
            "查询成功代码：200  \n" +
            "查询成功要求列举:  \n" +
            "1.域和token一定要匹配【对应异常代码：1001】  \n" +
            "2.你输入的域下此属主不存在【对应异常代码：1002】  \n" +
            "3.属主若没有拥有任何资源【对应异常代码：2007】  \n")
    @RequestMapping(value="/queryrestreemax",method = RequestMethod.GET)
    public ResultUtils queryResTreeMax(@RequestParam(value="domId",required = true) Integer domId,
                                       @RequestParam(value="ownerId",required = true) Integer ownerId,
                                       @RequestHeader(value = "Validate", required = true) String validate){
        //验证varify
        //属主是否存在
        //其中属主是否存在查的是基本表
        Integer varifycode  = domainMapper.varify(domId,validate);
        if(varifycode!=1){
            throw new FailedException(ResponseInfo.AUTH_FAILED.getErrorCode(),
                    "不能查询域"+domId+"下的属主"+ownerId+"拥有的资源种类，因为"+
                    ResponseInfo.AUTH_FAILED.getErrorMsg());
        }else if(!(domResOwnerService1.ownerExist(domId,ownerId))){
            throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(),
                    "查询失败，因为域"+domId+"下的属主"+ownerId+"不存在");
        }else{
            return domOwnerResMaxService.getOwnerMaxRess(domId,ownerId);
        }

    }
}
