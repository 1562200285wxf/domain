package cn.itsmith.sysutils.resacl.controller;




import cn.itsmith.sysutils.resacl.common.utilss.AddResourceType;
import cn.itsmith.sysutils.resacl.common.utilss.ChangeResourceTypeDes;
import cn.itsmith.sysutils.resacl.common.utilss.DeleteResourceType;
import cn.itsmith.sysutils.resacl.common.utilss.DomResTree;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.serviceImpl.DomResTypeServiceImpl;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value="资源种类",tags={"资源种类Api"})
@RestController
@RequestMapping("/DomResTypeController")
public class DomResTypeController {

    @Autowired
    DomResTypeServiceImpl resTypeServiceImpl;
    public DomResTypeController(DomResTypeServiceImpl resTypeServiceImpl) {
        this.resTypeServiceImpl = resTypeServiceImpl;
    }

    @ApiOperation(value = "添加域资源种类", notes = "注册域在特定的域下添加资源种类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domResType", value = "资源种类", dataType = "AddResourceType",required = false,paramType = "body"),
    })
    @RequestMapping(value="/addResourceType",method = RequestMethod.POST)
    public ResultUtils addResourceType(@RequestBody AddResourceType addResourceType){
        DomResType domResType = new DomResType();
        domResType.setDomId(addResourceType.getDomId());
        domResType.setResTypeId(addResourceType.getResTypeId());
        domResType.setPId(addResourceType.getPId());
        domResType.setResTypeDes(addResourceType.getResTypeDes());
        domResType.setResName(addResourceType.getResName());
        domResType.setStatus(1);
        ResultUtils result = resTypeServiceImpl.addResourceType(domResType);
        return result;
    }

    @ApiOperation(value = "修改域资源种类描述", notes = "修改域资源种类描述")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "changeResourceTypeDes", value = "修改域资源种类描述", dataType = "ChangeResourceTypeDes",required = false,paramType = "body"),
    })
    @RequestMapping(value="/updateResourceTypeDes",method = RequestMethod.POST)
    public ResultUtils updateResourceTypeDes(@RequestBody ChangeResourceTypeDes changeResourceTypeDes){
        DomResType domResType = new DomResType();
        domResType.setDomId(changeResourceTypeDes.getDomid());
        domResType.setResTypeDes(changeResourceTypeDes.getRestypedes());
        domResType.setResTypeId(changeResourceTypeDes.getRestypeid());
        ResultUtils result = resTypeServiceImpl.changeResourceTypeDes(domResType);
        return result;

    }

    @ApiOperation(value = "删除域资源种类", notes = "删除域资源种类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deleteResourceType", value = "删除资源种类", dataType = "DeleteResourceType",required = false,paramType = "body"),
    })
    @RequestMapping(value="/deleteResourceType",method = RequestMethod.POST)
    public ResultUtils deleteResourceType(@RequestBody DeleteResourceType deleteResourceType){
        DomResType domResType = new DomResType();
        domResType.setDomId(deleteResourceType.getDomid());
        domResType.setResTypeId(deleteResourceType.getRestypeid());
        return resTypeServiceImpl.deleteResourceType(domResType);

    }

    @ApiOperation(value = "查询域内资源种类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domResTree", value = "域id", dataType = "DomResTree",required = false,paramType = "body"),
    })
    @RequestMapping(value="/getDomResTree",method = RequestMethod.POST)
    public ResultUtils getDomResTree( @RequestBody DomResTree domResTree){
        return resTypeServiceImpl.getDomResTree(domResTree.getDomid(),domResTree.getResTypeId());
    }


    @ApiOperation(value = "查询域内资源种类测试")
    @RequestMapping(value="/getDomResTreeTest",method = RequestMethod.GET)
    public ResultUtils getDomResTreeTest(){
        DomResTree domResTree1 = new DomResTree();
        domResTree1.setDomid(1);
        domResTree1.setResTypeId(0);
        return resTypeServiceImpl.getDomResTree(domResTree1.getDomid(),domResTree1.getResTypeId());
    }
}
