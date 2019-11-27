package cn.itsmith.sysutils.resacl.controller;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.common.utilss.TokenUtil;
import cn.itsmith.sysutils.resacl.common.utilss.UpdateDomainDes;
import cn.itsmith.sysutils.resacl.dao.DomainMapper;
import cn.itsmith.sysutils.resacl.entities.Domain;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value="域Controller",tags={"域的增删改查Api"})
@RestController
public class DomainController {

    @Autowired
    private  DomainMapper domainMapper;

    //1先判断域domid是否存在
    //2进行操作
    @ApiOperation(value = "注册域", notes = "针对注册域操作  \n" +
            "查询成功代码：200  \n" +
            "服务器异常 操作失败 9999 \n" +
            "域已经存在   2001  \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domain", value = "域", dataType = "Domain",required = false,paramType = "body"),
    })
    @RequestMapping(value="/registerDomain",method = RequestMethod.POST)
    public String registerDomain(@RequestBody Domain domain){
        Domain isDomain = domainMapper.selectByPrimaryKey(domain.getDomId());
        if(isDomain != null){
            throw new FailedException(ResponseInfo.DOMAIN_IS.getErrorCode(), ResponseInfo.DOMAIN_IS.getErrorMsg());
        }
       if(domainMapper.selectByPrimaryKey(domain.getDomId())==null){
           domainMapper.insertSelective(domain);
           if(domainMapper.selectByPrimaryKey(domain.getDomId()) != null)
           return "code : 200 域"+domain.getDomId()+"已经成功注册";
       }
        throw new FailedException(ResponseInfo.SERVER_ERROR.getErrorCode(), ResponseInfo.SERVER_ERROR.getErrorMsg());
    }

    //1先判断域domid是否存在
    //2生成Token
    //3判断是否成功
    @ApiOperation(value = "生成令牌", notes = "针对生成令牌域操作  \n" +
            "查询成功代码：200  \n" +
            "服务器异常 操作失败 9999 \n" +
            "域不存在   2001  \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/Token",method = RequestMethod.GET)
    public String produceDomainToken(Integer domid){
        if(domainMapper.selectByPrimaryKey(domid)!=null) {
            TokenUtil tokenUtil = new TokenUtil();
            Domain domain = new Domain();
            domain.setDomId(domid);
            String Token = tokenUtil.getRandom();
            domain.setDomToken(Token);
            domainMapper.updateByPrimaryKey(domain);
            if(domainMapper.selectByPrimaryKey(domid).getDomToken().equals(Token))
            return " code  200  域"+domain.getDomId()+"的令牌"+Token;
            throw new FailedException(ResponseInfo.SERVER_ERROR.getErrorCode(), ResponseInfo.SERVER_ERROR.getErrorMsg());
        }
        throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(), ResponseInfo.DOMAIN_NOT.getErrorMsg());
    }

    //  1判断domid是否符合类型？？？？？？？？？？？？？？？？
    // 2根据domid判断域是否存在
    @ApiOperation(value = "域查询",notes = "针对域查询域操作  \n" +
            "查询成功代码：200  \n" +
            "服务器异常 操作失败 9999 \n" +
            "域不存在   2001  \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/queryDomain",method = RequestMethod.GET)
    public Domain queryDomain(Integer domid) {
        Domain domain = domainMapper.selectByPrimaryKey(domid);
        if(domain == null){
            throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(), ResponseInfo.DOMAIN_NOT.getErrorMsg());
        }
           return domain;
    }

    //1先判断域是否存在
    //2进行删除操作
    //3判断是否成功
    @ApiOperation(value = "删除域",notes = "针对域查询域操作  \n" +
            "查询成功代码：200  \n" +
            "服务器异常 操作失败 9999 \n" +
            "域不存在   2001  \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/deleteDomain",method = RequestMethod.GET)
    public String deleteDomain(Integer domid){
        Domain domain = domainMapper.selectByPrimaryKey(domid);
        if(domain == null){
            throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(), ResponseInfo.DOMAIN_NOT.getErrorMsg());
        }
        if(domain!=null){
            domainMapper.deleteByPrimaryKey(domid);
            if(domainMapper.selectByPrimaryKey(domid)!=null){
                throw new FailedException(ResponseInfo.SERVER_ERROR.getErrorCode(), ResponseInfo.SERVER_ERROR.getErrorMsg());
            }
            else return " code  200  域"+domain.getDomId()+"已经成功删除";
        }
         throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(), ResponseInfo.DOMAIN_NOT.getErrorMsg());
    }

    //1验证域的存在性
    // 2进行对象移植，修改操作
    // 3查找是否操作成功
    @ApiOperation(value = "修改域描述", notes = "针对域查询域修改域描述操作  \n" +
            "修改成功代码：200  \n" +
            "服务器异常 操作失败 9999 \n" +
            "域不存在   2001  \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "updateDomainDes", value = "修改域描述", dataType = "UpdateDomainDes",required = false,paramType = "body"),
    })
    @RequestMapping(value="/updateDomainDes",method = RequestMethod.POST)
    public String modifyDomainDes(@RequestBody  UpdateDomainDes updateDomainDes){
        Domain domain = domainMapper.selectByPrimaryKey(updateDomainDes.getDomid());
        Domain updateDomain = new Domain();
        if(domain == null){
            throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(), ResponseInfo.DOMAIN_NOT.getErrorMsg());
        }
        if(domainMapper.selectByPrimaryKey(domain.getDomId())!=null) {
            updateDomain.setDomId(updateDomainDes.getDomid());
            updateDomain.setDomDes(updateDomainDes.getDomdes());
            domainMapper.updateByPrimaryKeySelective(updateDomain);
        }
        if(domainMapper.selectByPrimaryKey(updateDomain.getDomId()).getDomDes().equals(updateDomainDes.getDomdes()))
            return " code  200  域"+domain.getDomId()+"的描述已经成功修改为"+updateDomainDes.getDomdes();
            else
            throw new FailedException(ResponseInfo.SERVER_ERROR.getErrorCode(), ResponseInfo.SERVER_ERROR.getErrorMsg());
    }

}
