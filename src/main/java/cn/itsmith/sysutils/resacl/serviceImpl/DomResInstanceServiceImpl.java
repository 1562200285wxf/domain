package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomResInstanceMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomUserOperationMapper;
import cn.itsmith.sysutils.resacl.entities.DomResInstance;
import cn.itsmith.sysutils.resacl.entities.DomResOwner;
import cn.itsmith.sysutils.resacl.entities.DomResOwnerNode;
import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import cn.itsmith.sysutils.resacl.service.DomResInstanceService;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "DomResInstanceService")
public class DomResInstanceServiceImpl implements DomResInstanceService {

    @Autowired
    DomResInstanceMapper domResInstanceMapper;
    @Autowired
    DomUserOperationMapper domUserOperationMapper;
    @Autowired
    DomResOwnerMapper domResOwnerMapper;
    @Autowired
    DomResOwnerService domResOwnerService;

    /**
     * 注册属主资源实例
     * @param domResInstance
     * @return
     */
    @Override
    public ResultUtils addDomResInstance(DomResInstance domResInstance) {
        ResultUtils resultUtils = new ResultUtils();
        domResInstance.setStatus(1);
        if(domResInstanceMapper.insert(domResInstance)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功为标识为%d的域下标识为%d的属主中标识为%d的资源种类添加标识为%d的资源实例",
                    domResInstance.getDomId(), domResInstance.getOwnerId(), domResInstance.getResTypeId(), domResInstance.getResId()));
            resultUtils.setData(domResInstance.getResId());
        }else {
            throw new FailedException("未知错误，数据插入失败");
        }
        return resultUtils;
    }

    /**
     * 删除属主资源实例，同时删除资源授权
     * @param domResInstance
     * @return
     */
    @Override
    public ResultUtils deleteDomResInstance(DomResInstance domResInstance) {
        //查询资源授权
        List<DomUserOperation> domUserOperations = domUserOperationMapper.selectByResId1(
                domResInstance.getDomId(), domResInstance.getOwnerId(), domResInstance.getResTypeId(), domResInstance.getResId());
        //判断资源授权是否存在，存在就要删除
        if(domUserOperations.size()!=0){
            DomUserOperation domUserOperation = new DomUserOperation();
            domUserOperation.setDomId(domResInstance.getDomId());
            domUserOperation.setOwnerId(domResInstance.getOwnerId());
            domUserOperation.setResTypeId(domResInstance.getResTypeId());
            domUserOperation.setResId(domResInstance.getResId());
            domUserOperation.setStatus(0);
            domUserOperationMapper.updateStatus(domUserOperation);
        }
        ResultUtils resultUtils = new ResultUtils();
        //删除属主资源实例
        domResInstance.setStatus(0);
        if(domResInstanceMapper.updateStatus(domResInstance)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功删除标识为%d的域下标识为%d的属主中标识为%d的资源种类中标识为%d的资源实例",
                    domResInstance.getDomId(), domResInstance.getOwnerId(), domResInstance.getResTypeId(), domResInstance.getResId()));
            resultUtils.setData(domResInstance);
        }else {
            throw new FailedException("数据删除失败");
        }
        return resultUtils;
    }

    /**
     * 查询属主资源实例
     * @return
     */
    @Override
    public ResultUtils getOwnerResInstance(int domId, int ownerId) {
        DomResOwner domResOwner = domResOwnerMapper.selectById(domId, ownerId);
        //生成头节点
        DomResOwnerNode domResOwnerNode1 = new DomResOwnerNode(domResOwner);
        //递归生成子树
        DomResOwnerNode domResOwnerNode = domResOwnerService.createDomResOwnerTree(domResOwnerNode1);
        List<DomResOwner> domResOwners = new ArrayList<DomResOwner>();
        //获取到属主树中属主的信息
        domResOwners = getTreeDomResOwner(domResOwners, domResOwnerNode);
        List<DomResInstance> domResInstances = new ArrayList<DomResInstance>();
        //遍历属主和子属主查到他们的资源实例，加入到需要查找的属主的资源实例中
        for(DomResOwner domResOwner1 : domResOwners){
            List<DomResInstance> domResInstances1 = domResInstanceMapper.selectByOwnerId(domResOwner1.getOwnerId());
            for(DomResInstance domResInstance : domResInstances1)
                if(domResInstance.getStatus()==1)
                    domResInstances.add(domResInstance);
        }
        ResultUtils resultUtils = new ResultUtils(ResponseInfo.SUCCESS.getErrorCode(),
                String.format("成功获取标识为%d的域下标识为%d的属主的资源实例",
                        domId, ownerId), domResInstances);

        return resultUtils;
    }

    /**
     * 用于遍历属主树，找出树中的属主
     * @param domResOwners
     * @param domResOwnerNode
     * @return
     */
    private List<DomResOwner>  getTreeDomResOwner(List<DomResOwner> domResOwners, DomResOwnerNode domResOwnerNode){
        DomResOwner domResOwner = domResOwnerNode.getData();
        domResOwners.add(domResOwner);
        for(DomResOwnerNode domResOwnerNode1 : domResOwnerNode.getChildList()){
            getTreeDomResOwner(domResOwners, domResOwnerNode1);
        }
        return domResOwners;
    }


    /**
     * 判断实例是否存在，不存在就抛出异常
     * @param
     * @return
     */
    @Override
    public boolean resInstanceExist(int domId, int ownerId, int resTypeId, int resId) {
        DomResInstance domResInstance = new DomResInstance();
        domResInstance.setDomId(domId);
        domResInstance.setOwnerId(ownerId);
        domResInstance.setResTypeId(resTypeId);
        domResInstance.setResId(resId);
        DomResInstance domResInstance1=domResInstanceMapper.select(domResInstance);
//        if (domResInstance1==null){
//            throw new FailedException(ResponseInfo.GETRESINTANCE_ERROR);
//        }else{
        if(domResInstance1==null || domResInstance1.getStatus()==0)
            return false;
        else
            return true;
//        }
    }
}
