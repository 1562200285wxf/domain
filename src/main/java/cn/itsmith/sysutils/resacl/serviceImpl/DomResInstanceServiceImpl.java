package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomResInstanceMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomUserOperationMapper;
import cn.itsmith.sysutils.resacl.dao.InstanceMapper;
import cn.itsmith.sysutils.resacl.entities.*;
import cn.itsmith.sysutils.resacl.service.DomResInstanceService;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    InstanceMapper instanceMapper;
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
     * 添加多个实例
     * @param domResInstances
     * @return
     */
    @Override
    public ResultUtils addDomResInstances(List<DomResInstance> domResInstances) {
        ResultUtils resultUtils = new ResultUtils();
        for(DomResInstance domResInstance : domResInstances){
            domResInstance.setStatus(1);
            domResInstanceMapper.insert(domResInstance);
        }

        resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
        resultUtils.setMessage(String.format("成功为标识为%d的域下标识为%d的属主中标识为%d的资源种类添加%d个资源实例",
                domResInstances.get(0).getDomId(), domResInstances.get(0).getOwnerId(), domResInstances.get(0).getResTypeId(), domResInstances.size()));
        resultUtils.setData(domResInstances.size());
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
        if(domResInstanceMapper.updateStatus(domResInstance)!=0){
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
    public ResultUtils getOwnerTreeResInstance(int domId, int ownerId) {
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
            List<DomResInstance> domResInstances1 = domResInstanceMapper.selectByOwnerId(domResOwner1.getDomId(), domResOwner1.getOwnerId());
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
     * 获取属主下所有资源实例
     * @param domId
     * @param ownerId
     * @return
     */
    @Override
    public ResultUtils getOwnerInstance(int domId, int ownerId) {
        List<DomResInstance> domResInstances = domResInstanceMapper.selectByOwnerId(domId, ownerId);
        Iterator<DomResInstance> domResInstancesIt = domResInstances.iterator();
        while (domResInstancesIt.hasNext()) {
            DomResInstance domResInstance = domResInstancesIt.next();
            if (domResInstance.getStatus() == 0) {
                domResInstancesIt.remove();
            }
        }

        ResultUtils resultUtils = new ResultUtils(ResponseInfo.SUCCESS.getErrorCode(),
                String.format("成功获取标识为%d的域下标识为%d的属主的资源实例",
                        domId, ownerId), domResInstances);

        return resultUtils;
    }

    /**
     * 获取资源属主下某个种类下的资源实例
     * @param domId
     * @param ownerId
     * @param resTypeId
     * @return
     */
    @Override
    public ResultUtils getOwnerRTypeInstance(int domId, int ownerId, int resTypeId) {
        List<DomResInstance> domResInstances = domResInstanceMapper.beingUsed(domId, ownerId, resTypeId);
        Iterator<DomResInstance> domResInstancesIt = domResInstances.iterator();
        while (domResInstancesIt.hasNext()) {
            DomResInstance domResInstance = domResInstancesIt.next();
            if (domResInstance.getStatus() == 0) {
                domResInstancesIt.remove();
            }
        }
        DomResInstanceR domResInstanceR = new DomResInstanceR();
        List<TableData> tableData = new ArrayList<TableData>();
        //1代表桌子
        if(resTypeId==1){
            List<Desk> desks = instanceMapper.selectAllDesk();
            List<Desk> desks1 = new ArrayList<Desk>();
            for(Desk desk : desks) {
                for (DomResInstance domResInstance : domResInstances) {
                    if (domResInstance.getResId().equals(desk.getResId())) {
                        desks1.add(desk);
                    }
                }
            }

            TableData tableData1 = new TableData("编号", "resId");
            tableData.add(tableData1);
            TableData tableData2 = new TableData("名称", "name");
            tableData.add(tableData2);
            TableData tableData3 = new TableData("描述", "description");
            tableData.add(tableData3);
            TableData tableData4 = new TableData("形状", "shape");
            tableData.add(tableData4);
            domResInstanceR.setTableData(tableData);
            domResInstanceR.setDomResInstances(Collections.singletonList(desks1));
        }
        //2代表房间
        if(resTypeId==2){
            List<Room> rooms = instanceMapper.selectAllRoom();
            List<Room> rooms1 = new ArrayList<Room>();;
            for(Room room : rooms){
                for(DomResInstance domResInstance : domResInstances){
                    if(domResInstance.getResId().equals(room.getResId())){
                        rooms1.add(room);
                    }
                }
            }
            TableData tableData1 = new TableData("编号", "resId");
            tableData.add(tableData1);
            TableData tableData2 = new TableData("名称", "name");
            tableData.add(tableData2);
            TableData tableData3 = new TableData("描述", "description");
            tableData.add(tableData3);
            TableData tableData4 = new TableData("拥有桌子数量", "deskNo");
            tableData.add(tableData4);
            TableData tableData5 = new TableData("可容纳人数", "volume");
            tableData.add(tableData5);
            domResInstanceR.setTableData(tableData);
            domResInstanceR.setDomResInstances(Collections.singletonList(rooms1));
        }
        ResultUtils resultUtils = new ResultUtils(ResponseInfo.SUCCESS.getErrorCode(),
                String.format("成功获取标识为%d的域下标识为%d的属主下标识为%d的种类的资源实例",
                        domId, ownerId, resTypeId), domResInstanceR);
        return resultUtils;
    }

    /**
     * 通过资源类型id到特定的资源表里面查找到，再把改属主已经拥有的剔除。
     * @param domId
     * @param ownerId
     * @param resTypeId
     * @return
     */
    @Override
    public ResultUtils getBaseRTypeInstance(int domId, int ownerId, int resTypeId) {
        List<DomResInstance> domResInstances = domResInstanceMapper.beingUsed(domId, ownerId, resTypeId);
        Iterator<DomResInstance> domResInstancesIt = domResInstances.iterator();
        while (domResInstancesIt.hasNext()) {
            DomResInstance domResInstance = domResInstancesIt.next();
            if (domResInstance.getStatus() == 0) {
                domResInstancesIt.remove();
            }
        }
        ResultUtils resultUtils = new ResultUtils();
        if(resTypeId == 1){
            List<Desk> desks = instanceMapper.selectAllDesk();
            Iterator<Desk> desksIt = desks.iterator();
            while (desksIt.hasNext()) {
                Desk desk = desksIt.next();
                for(DomResInstance domResInstance : domResInstances){
                    if (desk.getResId().equals(domResInstance.getResId())) {
                        desksIt.remove();
                        break;
                    }
                }
            }
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setData(desks);
            resultUtils.setMessage("成功获取该资源种类下的资源实例");
            return resultUtils;
        }else if(resTypeId==2){
            List<Room> rooms = instanceMapper.selectAllRoom();
            Iterator<Room> roomsIt = rooms.iterator();
            while (roomsIt.hasNext()) {
                Room room = roomsIt.next();
                for(DomResInstance domResInstance : domResInstances){
                    if (room.getResId().equals(domResInstance.getResId())) {
                        roomsIt.remove();
                        break;
                    }
                }
            }
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setData(rooms);
            resultUtils.setMessage("成功获取该资源种类下的资源实例");
            return resultUtils;
        }
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
