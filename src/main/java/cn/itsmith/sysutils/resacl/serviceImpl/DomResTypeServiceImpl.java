package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerResMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.service.DomResTypeService;
import cn.itsmith.sysutils.resacl.service.DomainService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "DomResTypeService")
public class DomResTypeServiceImpl implements DomResTypeService {
    @Autowired
    DomResTypeMapper domResTypeMapper;

    /**
     *判断资源种类是否存在
     * @param domId
     * @param resTypeId
     * @return
     */
    @Override
    public boolean domResTypeExist(int domId, int resTypeId) {

        DomResType domResType = domResTypeMapper.selectById(domId, resTypeId);
//        if(domResType == null){
//            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
//                    String.format("域标识为%d的域下不存在标识为%d的资源种类",
//                            domId, resTypeId));
//        }else{
        if(domResType==null || domResType.getStatus()==0)
            return false;
        else
            return true;
//        }
    }

    //wang
    @Autowired
    DomainService domainService;
    @Autowired
//    DomainServiceImpl domainService;
//    DomResTypeMapper  domResTypeMapper;
            DomOwnerResMapper domOwnerResMapper;
//    public  DomResTypeServiceImpl(DomResTypeMapper domResTypeMapper,DomainServiceImpl domainService,DomOwnerResMapper domOwnerResMapper){
//        this.domResTypeMapper = domResTypeMapper;
//        this.domainService = domainService;
//        this.domOwnerResMapper = domOwnerResMapper;
//    }

    //1 判断域是否存在
    //2 判断此类是否已经存在
    //3判断插入,比较根资源种类
    // 1 如果不是根节点，则种类id和pid都不是0
    //2 如果是根节点，则必须都是0
    //4判断插入种类的根节点是偶存在。
    @Override
    public ResultUtils addResourceType(DomResType domResType) {
        ResultUtils resultUtils = new ResultUtils();
        boolean type = domainService.isDomain(domResType.getDomId());
        if(!type){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage("域标识为"+domResType.getDomId()+"的不存在");
            return resultUtils;
        }
        if(domResTypeMapper.selectByPrimaryKey(domResType.getResTypeId()) != null){
            resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
            resultUtils.setMessage(String.format("种类为标识为%d的种类已经是t_dom_res_type成员,，添加失败",domResType.getResTypeId()));
            return resultUtils;
        }
        if(domResType.getResTypeId()==0){
            if(domResType.getPId()!=0) {
                resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
                resultUtils.setMessage("资源种类为"+domResType.getResTypeId()+"添加失败节点为0的父节点也必须是0");
                return resultUtils;
            }
            if(domResType.getPId()==0) {
                domResTypeMapper.insertSelective(domResType);
                resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
                resultUtils.setMessage(String.format("种类为标识为%d的种类已经是t_dom_res_type成员",domResType.getResTypeId()));
                return resultUtils;
            }
        }
        if( domResType.getResTypeId()!=0 && domResTypeMapper.selectByPrimaryKey(domResType.getPId())==null ){
            resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
            resultUtils.setMessage(String.format("种类为标识为%d的种类的父级节点%d不存在",domResType.getResTypeId(),domResType.getPId()));
            return resultUtils;
        }
        domResTypeMapper.insertSelective(domResType);
        resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
        resultUtils.setMessage(String.format("种类为标识为%d的种类已经是t_dom_res_type成员",domResType.getResTypeId()));
        return resultUtils;
    }

    //1 判断域是否存在
    //2 判断此类是否已经存在
    //3进行操作，返回结果
    @Override
    public ResultUtils changeResourceTypeDes(DomResType domResType) {
        ResultUtils resultUtils = new ResultUtils();
        boolean type = domainService.isDomain(domResType.getDomId());
        if(!type){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage(String.format("域标识为%d的不存在，节点%d添加失败",domResType.getDomId(),domResType.getResTypeId()));
            return resultUtils;
        }
        if(domResTypeMapper.selectByPrimaryKey(domResType.getResTypeId()) == null){
            resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
            resultUtils.setMessage(String.format("种类为标识为%d的种类不是t_dom_res_type成员,，修改失败",domResType.getResTypeId()));
            return resultUtils;
        }
        domResTypeMapper.updateByPrimaryKeySelective(domResType);
        resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
        resultUtils.setMessage(String.format("种类为标识为%d的种类不是t_dom_res_type成员,，修改失败",domResType.getResTypeId()));
        return resultUtils;
    }

    @Override
    public ResultUtils deleteResourceType(DomResType domResType) {
        ResultUtils resultUtils = new ResultUtils();
        boolean type = domainService.isDomain(domResType.getDomId());
        if(!type){
            resultUtils.setCode(ResponseInfo.DOMAIN_NOT.getErrorCode());
            resultUtils.setMessage(String.format("域标识为%d的不存在，节点%d删除失败",domResType.getDomId(),domResType.getResTypeId()));
            return resultUtils;
        }
        if(domResTypeMapper.selectByPrimaryKey(domResType.getResTypeId()) == null){
            resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
            resultUtils.setMessage(String.format("种类为标识为%d的种类不是t_dom_res_type成员,，删除失败",domResType.getResTypeId()));
            return resultUtils;
        }
        //如果此节点的id项不是任何资源种类的父节点，才能继续判断
        List<DomResType> allDomResTypes=getDomResType(domResType.getDomId());
        for (DomResType item : allDomResTypes) {
            if (item.getPId() ==domResType.getResTypeId() ) {
                resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
                resultUtils.setMessage(String.format("种类为标识为%d的种类有用，删除失败",domResType.getResTypeId()));
                return resultUtils;
            }
        }
        //如果在属组拥有资源里面有，则不可以删除。
        if(domOwnerResMapper.selectByPrimaryKey(domResType.getResTypeId()) != null) {
            resultUtils.setCode(ResponseInfo.FALSE_IS.getErrorCode());
            resultUtils.setMessage(String.format("种类为标识为%d的种类有用，删除失败",domResType.getResTypeId()));
            return resultUtils;
        }
        domResTypeMapper.deleteByPrimaryKey(domResType.getResTypeId());
        resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
        resultUtils.setMessage(String.format("种类为标识为%d的种类，删除成功",domResType.getResTypeId()));
        resultUtils.setData(domResType);
        return resultUtils;
    }

    public ResultUtils getDomResTypes(Integer domid){
        ResultUtils resultUtils = new ResultUtils();
        boolean type = domainService.isDomain(domid);
        if(!type){
            throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(),"域"+domid+ ResponseInfo.DOMAIN_NOT.getErrorMsg());
        }
        List<DomResType> list = domResTypeMapper.getDomResTypes(domid);
        resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
        resultUtils.setMessage(String.format("域标识为%d的所有种类，删除成功",domid));
        resultUtils.setData(list);
        return resultUtils;
    }
    public List<DomResType>   getDomResType(Integer domid){
        return domResTypeMapper.getDomResTypes(domid);
    }
}
