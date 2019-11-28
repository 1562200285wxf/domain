package cn.itsmith.sysutils.resacl.controller;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.common.utilss.TokenUtil;
import cn.itsmith.sysutils.resacl.common.utilss.UpdateDomainDes;
import cn.itsmith.sysutils.resacl.common.utilss.addDomain;
import cn.itsmith.sysutils.resacl.dao.DomainMapper;
import cn.itsmith.sysutils.resacl.entities.Domain;
import cn.itsmith.sysutils.resacl.service.DomainService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value="域Controller",tags={"域的增删改查Api"})
@RestController
@RequestMapping("/DomainController")
public class DomainController {

    @Autowired
    DomainMapper domainMapper;

    @Autowired
    DomainService domainService;
    //1先判断域domid是否存在
    //2进行操作
    @ApiOperation(value = "注册域", notes = "针对注册域操作  \n" +
            "200  注册成功\n" +
            "9999 服务器异常 操作失败\n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domain", value = "域", dataType = "Domain",required = false,paramType = "body"),
    })
    @RequestMapping(value="/registerDomain",method = RequestMethod.GET)
    public ResultUtils registerDomain(addDomain adddomain){
        //默认初始域令牌123456
        Domain domain = new Domain();
        domain.setDomDes(adddomain.getDomDes());
        domain.setDomName(adddomain.getDomName());
        domain.setDomToken("123456");
        return domainService.addDomain(domain);
    }

    //1先判断域domid是否存在
    //2生成Token
    //3判断是否成功
    @ApiOperation(value = "生成令牌", notes = "针对生成令牌域操作  \n" +
            "2002 域不存在，操作失败\n" +
            "9999 服务器异常 操作失败\n" +
            "2003 操作成功 \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/Token",method = RequestMethod.GET)
    public ResultUtils produceDomainToken(Integer domid){
        ResultUtils resultUtils = new ResultUtils();
        Domain domain = domainMapper.selectByPrimaryKey(domid);
        if(domain == null){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage(String.format("域标识为%d的不存在", domid));
            return resultUtils;
        }
        if(domain!=null) {
            TokenUtil tokenUtil = new TokenUtil();
            String Token = tokenUtil.getRandom();
            domain.setDomToken(Token);
            domainMapper.updateByPrimaryKeySelective(domain);
            resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
            resultUtils.setMessage(String.format("成功为标识为%d的域下添加域token",
                    domid)+Token);
            resultUtils.setData(domain.getDomId());
        }
        throw new FailedException(ResponseInfo.FALSE_IS.getErrorCode(), ResponseInfo.FALSE_IS.getErrorMsg());
    }

    //  1判断domid是否符合类型？？？？？？？？？？？？？？？？
    // 2根据domid判断域是否存在
    @ApiOperation(value = "域查询",notes = "针对域查询域操作  \n" +
            "2003 操作成功\n" +
            "服务器异常 操作失败 9999 \n" +
            "2002 域不存在 操作失败\n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/queryDomain",method = RequestMethod.GET)
    public ResultUtils queryDomain(Integer domid) {
        ResultUtils resultUtils = new ResultUtils();
        Domain domain = domainMapper.selectByPrimaryKey(domid);
        if(domain == null){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage(String.format("域标识为%d的不存在", domid));
            return resultUtils;
        }
        resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
        resultUtils.setMessage("成功查询域id"+"domid");
        resultUtils.setData(domain);
        return resultUtils;
    }

    //1先判断域是否存在
    //2进行删除操作
    //3判断是否成功
    @ApiOperation(value = "删除域",notes = "针对域删除域操作  \n" +
            "删除成功代码：2003  \n" +
            "服务器异常 操作失败 9999 \n" +
            "域不存在   2001  \n" +
            "...")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "domid", value = "域id", dataType = "Integer",required = false,paramType = "query"),
    })
    @RequestMapping(value="/deleteDomain",method = RequestMethod.GET)
    public ResultUtils deleteDomain(Integer domid){
        ResultUtils resultUtils = new ResultUtils();
        Domain domain = domainMapper.selectByPrimaryKey(domid);
        if(domain == null){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage(String.format("域标识为%d的不存在", domid));
            return resultUtils;
        }
        if(domain!=null){
            domain.setStatus(0);
            domainMapper.updateByPrimaryKeySelective(domain);
            resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
            resultUtils.setMessage("成功删除域id"+domain.getDomId());
            resultUtils.setData(domain.getDomId());
            return resultUtils;
        }
        throw new FailedException(ResponseInfo.FALSE_IS.getErrorCode(), "删除"+ResponseInfo.FALSE_IS.getErrorMsg()+domain.getDomId());
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
    public ResultUtils modifyDomainDes(@RequestBody  UpdateDomainDes updateDomainDes) {
        ResultUtils resultUtils = new ResultUtils();
        Domain domain = domainMapper.selectByPrimaryKey(updateDomainDes.getDomid());
        Domain updateDomain = new Domain();
        if(domain == null){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage("域标识为"+updateDomainDes.getDomid()+"的不存在");
            return resultUtils;
        }
        if (domain != null) {
            updateDomain.setDomId(updateDomainDes.getDomid());
            updateDomain.setDomDes(updateDomainDes.getDomdes());
            domainMapper.updateByPrimaryKeySelective(updateDomain);
            resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改标识为%d的域的描述为",
                    updateDomainDes.getDomid())+updateDomainDes.domdes);
            resultUtils.setData(updateDomainDes.getDomid());
            return resultUtils;
        } else
            throw new FailedException(ResponseInfo.SERVER_ERROR.getErrorCode(), "修改失败"+updateDomainDes.getDomid());
    }

}
