package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerResMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.service.DomResTypeService;
import cn.itsmith.sysutils.resacl.service.DomainService;
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
    public String addResourceType(DomResType domResType) {
        boolean type = domainService.isDomain(domResType.getDomId());
        if(!type){
            return "域"+domResType.getDomId()+"不存在";
        }
        if(domResTypeMapper.selectByPrimaryKey(domResType.getResTypeId()) != null){
            return "此种类"+domResType.getResTypeId()+"已经存在";
        }
        if(domResType.getResTypeId()==0){
            if(domResType.getPId()!=0) {
                return "节点为0的父节点也必须是0";
            }
            if(domResType.getPId()==0) {
                domResTypeMapper.insertSelective(domResType);
                return "种类"+domResType.getResTypeId()+"是t_dom_res_type成员";
            }
        }
        if( domResType.getResTypeId()!=0 && domResTypeMapper.selectByPrimaryKey(domResType.getPId())==null ){
            return "父级节点"+domResType.getPId()+"不存在";
        }
        domResTypeMapper.insertSelective(domResType);
        return "域"+domResType.getDomId()+"资源种类"+domResType.getResTypeId()+"成功添加";
    }

    //1 判断域是否存在
    //2 判断此类是否已经存在
    //3进行操作，返回结果
    @Override
    public String changeResourceTypeDes(DomResType domResType) {
        boolean type = domainService.isDomain(domResType.getDomId());
        if(!type){
            return "域"+domResType.getDomId()+"不存在";
        }
        if(domResTypeMapper.selectByPrimaryKey(domResType.getResTypeId()) == null){
            return "此种类"+domResType.getResTypeId()+"不存在";
        }
        domResTypeMapper.updateByPrimaryKeySelective(domResType);
        return "域"+domResType.getDomId()+"资源种类描述"+domResType.getResTypeDes()+"成功修改";
    }

    @Override
    public String deleteResourceType(DomResType domResType) {
        boolean type = domainService.isDomain(domResType.getDomId());
        if(!type){
            return "域"+domResType.getDomId()+"不存在";
        }
        if(domResTypeMapper.selectByPrimaryKey(domResType.getResTypeId()) == null){
            return "此种类"+domResType.getResTypeId()+"不存在";
        }
        //如果此节点的pid项不是任何资源种类的子节点，才能继续判断
        List<DomResType> allDomResTypes=getDomResTypes(domResType.getDomId());
        for (DomResType item : allDomResTypes) {
            if (item.getPId() ==domResType.getResTypeId() ) {
                return domResType.getResTypeId()+"资源种类有用，不可以删除";
            }
        }
        //如果在属组拥有资源里面有，则不可以删除。
        if(domOwnerResMapper.selectByPrimaryKey(domResType.getResTypeId()) != null) {
            return domResType.getResTypeId()+"资源种类有用，不可以删除";
        }
        domResTypeMapper.deleteByPrimaryKey(domResType.getResTypeId());
        return domResType.getResTypeId()+"资源种类,已经从表资源属组删除";
    }

    public List<DomResType> getDomResTypes(Integer domid){
        boolean type = domainService.isDomain(domid);
        if(!type){
            throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(),"域"+domid+ ResponseInfo.DOMAIN_NOT.getErrorMsg());
        }
        return domResTypeMapper.getDomResTypes(domid);
    }
}
