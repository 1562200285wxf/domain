package cn.itsmith.sysutils.resacl.controller;




import cn.itsmith.sysutils.resacl.common.utilss.ChangeResourceTypeDes;
import cn.itsmith.sysutils.resacl.common.utilss.DeleteResourceType;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.serviceImpl.DomResTypeServiceImpl;
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
public class ResTypeController {

    @Autowired
    DomResTypeServiceImpl resTypeServiceImpl;
    public ResTypeController(DomResTypeServiceImpl resTypeServiceImpl) {
        this.resTypeServiceImpl = resTypeServiceImpl;
    }

    @ApiOperation(value = "添加域资源种类", notes = "注册域在特定的域下添加资源种类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domResType", value = "资源种类", dataType = "DomResType",required = false,paramType = "body"),
    })
    @RequestMapping(value="/addResourceType",method = RequestMethod.POST)
    public String addResourceType(@RequestBody DomResType domResType){
        String result = resTypeServiceImpl.addResourceType(domResType);
        return result;
    }

    @ApiOperation(value = "修改域资源种类描述", notes = "修改域资源种类描述")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "changeResourceTypeDes", value = "修改域资源种类描述", dataType = "ChangeResourceTypeDes",required = false,paramType = "body"),
    })
    @RequestMapping(value="/changeResourceTypeDes",method = RequestMethod.POST)
    public String changeResourceTypeDes(@RequestBody ChangeResourceTypeDes changeResourceTypeDes){
        DomResType domResType = new DomResType();
        domResType.setDomId(changeResourceTypeDes.getDomid());
        domResType.setResTypeDes(changeResourceTypeDes.getRestypedes());
        domResType.setResTypeId(changeResourceTypeDes.getRestypeid());
        String result = resTypeServiceImpl.changeResourceTypeDes(domResType);
        return result;

    }

    @ApiOperation(value = "删除域资源种类", notes = "删除域资源种类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deleteResourceType", value = "删除资源种类", dataType = "DeleteResourceType",required = false,paramType = "body"),
    })
    @RequestMapping(value="/deleteResourceType",method = RequestMethod.POST)
    public String deleteResourceType(@RequestBody DeleteResourceType deleteResourceType){
        DomResType domResType = new DomResType();
        domResType.setDomId(deleteResourceType.getDomid());
        domResType.setResTypeId(deleteResourceType.getRestypeid());
        return resTypeServiceImpl.deleteResourceType(domResType);

    }

    @ApiOperation(value = "查询域内资源种类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/getDomResTypes",method = RequestMethod.GET)
    public List<DomResType> getDomResTypes(Integer domid){
        return resTypeServiceImpl.getDomResTypes(domid);
    }
}
