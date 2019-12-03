package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerResMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.entities.DomResTypeNode;
import cn.itsmith.sysutils.resacl.service.DomResTypeService;
import cn.itsmith.sysutils.resacl.service.DomainService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "DomResTypeService")
public class DomResTypeServiceImpl implements DomResTypeService {
    @Autowired
    DomResTypeMapper domResTypeMapper;

    @Autowired
    DomResTypeServiceImpl domResTypeService;

    /**
     *判断资源种类是否存在
     * @param domId
     * @param resTypeId
     * @return
     */
    @Override
    public boolean domResTypeExist(int domId, int resTypeId) {

        DomResType domResType = domResTypeMapper.getDomResTypeByResTypeId(domId, resTypeId);
//        if(domResType == null){
//            throw new FailedException(ResponseInfo.GETRESTYPE_ERROR.getErrorCode(),
//                    String.format("域标识为%d的域下不存在标识为%d的资源种类",
//                            domId, resTypeId));
//        }else{
        if(domResType==null)
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

    public ResultUtils getDomResTree(Integer domid,Integer resTypeId){
        ResultUtils resultUtils = new ResultUtils();
        boolean type = domainService.isDomain(domid);
        if(!type){
            throw new FailedException(ResponseInfo.DOMAIN_NOT.getErrorCode(),"域"+domid+ ResponseInfo.DOMAIN_NOT.getErrorMsg());
        }
        if(resTypeId==0){
            DomResType DomResTypeRoot = new DomResType();
            DomResTypeRoot.setResTypeId(0);
            DomResTypeRoot.setPId(0);
            DomResTypeRoot.setDomId(domid);
            DomResTypeNode domResTypeNode = new DomResTypeNode(DomResTypeRoot);
            DomResTypeNode  domResTypeTree =domResTypeService.createDomResTypeTree(domResTypeNode);
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format(
                    "成功获取标识为%d的域下所有属主树", domid));
            resultUtils.setData(domResTypeTree);
            return resultUtils;
        }
        //创建初始根节点
        DomResType DomResTypeRoot = new DomResType();
        DomResTypeRoot.setResTypeId(0);
        DomResTypeRoot.setPId(0);
        DomResTypeRoot.setDomId(domid);
        DomResTypeNode domResTypeNode = new DomResTypeNode(DomResTypeRoot);
        DomResTypeNode  domResTypeTree =domResTypeService.createDomResTypeTree(domResTypeNode);

        resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
        resultUtils.setMessage(String.format("域标识为%d的所有种类，获取成功",domid));
        resultUtils.setData(domResTypeTree);
        return resultUtils;
    }
    public List<DomResType>   getDomResType(Integer domid){
        return domResTypeMapper.getDomResTypes(domid);
    }


    /**
     * 递归函数，传入树节点生成一个树，返回树的头节点
     * @return
     */
    public DomResTypeNode createDomResTypeTree(DomResTypeNode domResTypeNode) {
        int domId = domResTypeNode.getData().getDomId();
        int resTypeId = domResTypeNode.getData().getResTypeId();
        //找到子节点的资源属主
        List<DomResType> child = domResTypeMapper.getDomResTypesByPid(domId,resTypeId);
        //将子节点加入节点中
        for(DomResType domResType : child){
            DomResTypeNode domResTypeNode1 = new DomResTypeNode(domResType);
            domResTypeNode.getChildList().add(domResTypeNode1);
        }
        //递归遍历子节点，让每个子节点递归生成子树
        for(DomResTypeNode domResTypeNode1 : domResTypeNode.getChildList()){
            if(domResTypeNode1.getData() != null){
                createDomResTypeTree(domResTypeNode1);
            }
        }
        return domResTypeNode;
    }


}
