package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.dao.*;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.entities.*;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
@Service(value="DomResOwnerService")
public class DomResOwnerServiceImpl implements DomResOwnerService {
    @Autowired
    DomResOwnerMapper domResOwnerMapper;
    @Autowired
    DomainMapper domainMapper;
    @Autowired
    DomOwnerUserMapper domOwnerUserMapper;
    @Autowired
    DomResTypeMapper domResTypeMapper;
    @Autowired
    DomOwnerResMapper domOwnerResMapper;
    @Autowired
    DomUserOperationMapper domUserOperationMapper;

    @Override
    public ResultUtils getAllDomResOwner() {
        List<DomResOwner> list;
        ResultUtils resultUtils = new ResultUtils();
        list = domResOwnerMapper.getAllDomResOwner();
        resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
        resultUtils.setMessage(ResponseInfo.SUCCESS.getErrorMsg());
        resultUtils.setData(list);
        return resultUtils;
    }

    /**
     * 添加新的资源属主
     * @param domResOwner
     * @return
     */
    @Override
    public ResultUtils addDomResOwner(DomResOwner domResOwner) {
        ResultUtils resultUtils = new ResultUtils();
        domResOwner.setStatus(1);
        if(domResOwnerMapper.insert(domResOwner)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功为标识为%d的域添加标识为%d的属主",
                    domResOwner.getDomId(), domResOwner.getOwnerId()));
            resultUtils.setData(domResOwner.getOwnerId());
        }else {
            throw new FailedException(String.format("未知错误，为标识为%d的域添加属主失败",
                    domResOwner.getDomId()));
        }
        return resultUtils;
    }

    /**
     * 根据id修改资源属主的描述
     * @param domResOwner
     * @return
     */
    @Override
    public ResultUtils updateOwnerDes(DomResOwner domResOwner) {
        ResultUtils resultUtils = new ResultUtils();
        if(domResOwnerMapper.updateOwner(domResOwner)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改域标识为%d域下的标识为%d的属主描述为%s",
                    domResOwner.getDomId(), domResOwner.getOwnerId(), domResOwner.getOwnerDes()));
            domResOwner = domResOwnerMapper.selectById(domResOwner.getDomId(), domResOwner.getOwnerId());
            resultUtils.setData(domResOwner);
        }else{
            throw new FailedException(String.format("未知错误，未能修改标识为%d的域下标识为%d的属主的描述",
                    domResOwner.getDomId(), domResOwner.getOwnerId()));
        }

        return resultUtils;
    }

    /**
     * 根据id删除资源属主
     * @param domResOwner
     */
    @Override
    public ResultUtils deleteDomResOwner(DomResOwner domResOwner) {
        ResultUtils resultUtils = new ResultUtils();
        domResOwner.setStatus(0);
        if (domResOwnerMapper.updateStatus(domResOwner) == 1) {
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功删除标识为%d域下标识为%d的属主",
                    domResOwner.getDomId(),domResOwner.getOwnerId()));
            resultUtils.setData(domResOwner);
        } else {
            throw new FailedException(String.format("未知错误，没有成功删除标识为%d域下标识为%d的属主",
                    domResOwner.getDomId(),domResOwner.getOwnerId()));
        }
        return resultUtils;
    }

    /**
     * 获取资源属主树
     * @param domId
     * @param ownerId
     * @return
     */
    @Override
    public ResultUtils getDomResOwnerTree(int domId, int ownerId){
        if(ownerId==0){
            DomResOwner domResOwner = new DomResOwner();
            domResOwner.setDomId(domId);
            domResOwner.setOwnerId(ownerId);
            DomResOwnerNode domResOwnerNode1 = new DomResOwnerNode(domResOwner);
            DomResOwnerNode domResOwnerNode = createDomResOwnerTree(domResOwnerNode1);
            ResultUtils resultUtils = new ResultUtils();
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format(
                    "成功获取标识为%d的域下所有属主树", domId));
            resultUtils.setData(domResOwnerNode);
            return resultUtils;
        }
        DomResOwner domResOwner = domResOwnerMapper.selectById(domId, ownerId);
        //生成头节点
        DomResOwnerNode domResOwnerNode1 = new DomResOwnerNode(domResOwner);
        //递归生成子树
        DomResOwnerNode domResOwnerNode = createDomResOwnerTree(domResOwnerNode1);
        ResultUtils resultUtils = new ResultUtils();
        resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
        resultUtils.setMessage(String.format(
                "成功获取标识为%d的域下的标识为%d的属主的属主树", domId, ownerId));
        resultUtils.setData(domResOwnerNode);
        return resultUtils;
    }

    /**
     * 递归函数，传入树节点生成一个树，返回树的头节点
     * @param domResOwnerNode
     * @return
     */
    @Override
    public DomResOwnerNode createDomResOwnerTree(DomResOwnerNode domResOwnerNode) {
        int domId = domResOwnerNode.getData().getDomId();
        int ownerId = domResOwnerNode.getData().getOwnerId();
        //找到子节点的资源属主
        List<DomResOwner> child = domResOwnerMapper.selectByPId(domId, ownerId);
        //将子节点加入节点中
        for(DomResOwner domResOwner1 : child){
            if(domResOwner1.getStatus()==1){
                DomResOwnerNode domResOwnerNode1 = new DomResOwnerNode(domResOwner1);
                domResOwnerNode.getChildList().add(domResOwnerNode1);
            }
        }
        //递归遍历子节点，让每个子节点递归生成子树
        for(DomResOwnerNode domResOwnerNode1 : domResOwnerNode.getChildList()){
            if(domResOwnerNode1.getData() != null){
                createDomResOwnerTree(domResOwnerNode1);
            }
        }
        return domResOwnerNode;
    }

    @Override
    public ResultUtils getOperationOwners(List<DomResOperationL> domResOperationLS) {
        Integer domId = domResOperationLS.get(0).getDomId();
        Integer ownerId = domResOperationLS.get(0).getOwnerId();
        //根据域id和属主id查询到该属主下的所有成员
        List<DomResOwner> domResOwners = domResOwnerMapper.selectByPId(domId, ownerId);
        //遍历操作，剔除拥有该操作的成员
        for(DomResOperationL domResOperationL : domResOperationLS){
            DomUserOperation domUserOperation = new DomUserOperation();
            domUserOperation.setDomId(domResOperationL.getDomId());
            domUserOperation.setOwnerId(domResOperationL.getOwnerId());
            domUserOperation.setResTypeId(domResOperationL.getResTypeId());
            domUserOperation.setOpId(domResOperationL.getOpId());
            domUserOperation.setResId(domResOperationL.getResId());
            domUserOperation.setTypes(1);
            List<DomUserOperation> domUserOperations = domUserOperationMapper.selectUsersOrOwners(domUserOperation);
            //遍历剔除
            Iterator<DomResOwner> domResOwnerIterator = domResOwners.iterator();
            while(domResOwnerIterator.hasNext()){
                DomResOwner domResOwner = domResOwnerIterator.next();
                if(domResOwner.getStatus()==0){
                    domResOwnerIterator.remove();
                }else{
                    for(DomUserOperation domUserOperation1 : domUserOperations){
                        if(domUserOperation1.getUserOwnerId().equals(domResOwner.getOwnerId())){
                            domResOwnerIterator.remove();
                        }
                    }
                }
            }
        }
        ResultUtils resultUtils = new ResultUtils();
        resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
        resultUtils.setMessage(String.format("成功获取属主"));
        resultUtils.setData(domResOwners);
        return resultUtils;
    }

    /**
     * 资源属主标识判断是否存在此资源属主，不存在就抛出异常
     *
     */
    @Override
    public boolean ownerExist(int domId, int ownerId){
        DomResOwner domResOwner = domResOwnerMapper.selectById(domId, ownerId);
//        if(domResOwner == null){
//            throw new FailedException(ResponseInfo.GETOWNER_ERROR.getErrorCode(),
//                    String.format("域标识为%d的域下不存在标识为%d的属主",domId,ownerId));
//        }else{
        if(domResOwner==null || domResOwner.getStatus()==0)
            return false;
        else
            return true;
//        }
    }

    /**
     * 查询此pid对应的资源属主是否使用中,使用中就抛出异常
     * @param domId
     * @return
     */
    @Override
    public void ownerUsing(int domId, int ownerId) {
        //根据资源属主标识查找所有属主中有没有以该节点为父节点
        List<DomResOwner> domResOwners = domResOwnerMapper.selectByPId(domId, ownerId);
        Iterator<DomResOwner> domResOwnersIt = domResOwners.iterator();
        while(domResOwnersIt.hasNext()){
            DomResOwner domResOwner = domResOwnersIt.next();
            if(domResOwner.getStatus()==0){
                domResOwnersIt.remove();
            }
        }
        if(domResOwners.size() != 0) {
            String ownersId = "";
            for(DomResOwner domResOwner : domResOwners)
                ownersId = String.format("%s %d", ownersId, domResOwner.getOwnerId());
            throw new FailedException(ResponseInfo.OWNERUSINGP_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下的标识为%d的属主还是标识为%s属主的父属主,不能删除",
                    domId, ownerId, ownersId));
        }
        //看属主成员中有没有存在该属主的成员
        List<DomOwnerUser> domOwnerUsers = domOwnerUserMapper.selectByDomId(ownerId);
        Iterator<DomOwnerUser> domOwnerUsersIt = domOwnerUsers.iterator();
        while(domOwnerUsersIt.hasNext()){
            DomOwnerUser domOwnerUser = domOwnerUsersIt.next();
            if(domOwnerUser.getStatus()==0){
                domOwnerUsersIt.remove();
            }
        }
        if(domOwnerUsers.size()!= 0){
            String usersId = "";
            for(DomOwnerUser domOwnerUser : domOwnerUsers)
                usersId = String.format("%s %d", usersId, domOwnerUser.getUserId());
            throw new FailedException(ResponseInfo.OWNERUSINGU_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下的标识为%d的属主还存在标识为%s的成员,不能删除",
                            domId, ownerId, usersId));
        }
        //看该属主是否拥有资源种类
        List<DomOwnerRes> domOwnerRess = domOwnerResMapper.selectByOwnerId(ownerId);
        Iterator<DomOwnerRes> domOwnersRessIt = domOwnerRess.iterator();
        while(domOwnersRessIt.hasNext()){
            DomOwnerRes domOwnerRes = domOwnersRessIt.next();
            if(domOwnerRes.getStatus()==0){
                domOwnersRessIt.remove();
            }
        }
        if(domOwnerRess.size()!= 0){
            String typesId = "";
            for(DomOwnerRes domOwnerRes1 : domOwnerRess)
                typesId = String.format("%s %d", typesId, domOwnerRes1.getResTypeId());
            throw new FailedException(ResponseInfo.OWNERUSINGT_ERROR.getErrorCode(),
                    String.format("域标识为%d的域下的标识为%d的属主还存在标识为%s的资源种类,不能删除",
                            domId, ownerId, typesId));
        }
    }
}
