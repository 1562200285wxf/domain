package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.*;
import cn.itsmith.sysutils.resacl.entities.*;

import cn.itsmith.sysutils.resacl.service.DomUserOperationService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import cn.itsmith.sysutils.resacl.utils.OperationDetail;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value="DomUserOperationService")
public class DomUserOperationServiceImpl implements DomUserOperationService{
    @Autowired
    DomUserOperationMapper domUserOperationMapper;

    @Autowired
    DomResOwnerMapper domResOwnerMapper;

    @Autowired
    DomOwnerResMapper domOwnerResMapper;

    @Autowired
    DomUserOperationServiceImpl domUserOperationServiceImpl;


    ResultUniteServiceImp resultUniteServiceImp = new ResultUniteServiceImp();
@Autowired
    DomResTypeMapper domResTypeMapper;

@Autowired
    InstanceMapper instanceMapper;
@Autowired
DomResOperationMapper domResOperationMapper;

    @Override
    public ResultUtils selectOps(DomUserOperation domUserOperation) {
        Integer domId = domUserOperation.getDomId();

        List<DomUserOperation> domUserOperation1 =
                domUserOperationMapper.selectOps(domUserOperation);
//if(domUserOperation1.size()!=0){
//    for (DomUserOperation domUserOperation2:
//    domUserOperation1) {
//        if(domUserOperation2.getStatus().equals(1)){
//            hasOps=true;
//
//        }
//    }
//}
        if(domUserOperation1.size()==0){
            throw new FailedException(ResponseInfo.NONOPERATION_ERROR.getErrorCode(),
                    "查询资源授权失败，因为域"+domUserOperation.getDomId()+"下的属主"+
                            domUserOperation.getOwnerId()+"下成员"+domUserOperation.getUserOwnerId()+
                            "对资源类型"+domUserOperation.getResTypeId()+"的实例"+domUserOperation.getResTypeId()+
                            "没有任何资源授权记录"
            );
        }else{
//            List<DomUserOperation> domUserOperation3 = new ArrayList<DomUserOperation>();
//            for (DomUserOperation domUserOperation2:
//                    domUserOperation1) {
//                if(domUserOperation2.getStatus().equals(1)){
//                    domUserOperation3.add(domUserOperation2);
//                }
//            }
            List<OperationDetail> operationDetails = new ArrayList<OperationDetail>();
            for (DomUserOperation domUserOperation2:
                    domUserOperation1) {
                if(domUserOperation2.getStatus().equals(1)){
                    Integer resTypeId = domUserOperation2.getResTypeId();
                    Integer resId = domUserOperation2.getResId();
                    Integer opId = domUserOperation2.getOpId();
                    OperationDetail operationDetail = new OperationDetail();
                    //resTypeName
                    DomResType domResType = domResTypeMapper.queryResBase(domId, resTypeId);
                    String resTypeName = domResType.getResName();
                    operationDetail.setResTypeName(resTypeName);
                    //resName
                    if(resTypeId.equals(1)){
                        List<Desk> desks = instanceMapper.selectAllDesk();
                        for (Desk desk:
                        desks) {
                            Integer resId1 = desk.getResId();
                            if(resId.equals(resId1)){
                                String deskName = desk.getName();
                                operationDetail.setResName(deskName);
                            }
                        }
                    }else if(resTypeId.equals(2)){
                        List<Room> rooms = instanceMapper.selectAllRoom();
                        for (Room room:
                             rooms) {
                            Integer resId1 = room.getResId();
                            if(resId.equals(resId1)){
                                String roomName = room.getName();
                                operationDetail.setResName(roomName);
                            }

                        }
                    }

//opName

                    DomResOperation domResOperation = new DomResOperation();
                    domResOperation.setDomId(domId);
                    domResOperation.setResTypeId(resTypeId);
                    domResOperation.setOpId(opId);
                    DomResOperation select = domResOperationMapper.select(domResOperation);
                    String opName = select.getOpName();
                    operationDetail.setOpName(opName);

                    operationDetails.add(operationDetail);


                }
            }
            //return  resultUniteServiceImp.resultSuccess(domUserOperation3); //成功返回值
            return  resultUniteServiceImp.resultSuccess(operationDetails);
        }

    }

    /**
     *
     * @param domUserOperation
     * @return
     */
    //接收domUserOperation
    @Override
    public ResultUtils addOp(DomUserOperation domUserOperation) {
        DomUserOperation domUserOperation1 = domUserOperationMapper.selectByWhole(domUserOperation);
        if(domUserOperation1!=null){
            throw new FailedException(ResponseInfo.ALREADYOPERATION_ERROR.getErrorCode(),
                    "添加域"+domUserOperation.getDomId()+"下"+"属主"+domUserOperation.getOwnerId()+
                            "下的此UserOwner"+domUserOperation.getUserOwnerId()+"对资源类型"+domUserOperation.getResTypeId()
                            +"的实例"+domUserOperation.getResId()+"的资源授权失败，因为"+
                            ResponseInfo.ALREADYOPERATION_ERROR.getErrorMsg());
        }else{
            System.out.print("==========================="+domUserOperation1);
            System.out.print("==========================="+domUserOperation);
            domUserOperationMapper.insert(domUserOperation);
            ResultUtils resultUtils = new ResultUtils(ResponseInfo.SUCCESS.getErrorCode(),
                    ResponseInfo.SUCCESS.getErrorMsg(),
                    domUserOperation.getId()
            );
            return resultUtils;
            //return  resultUniteServiceImp.resultSuccess(domUserOperation); //成功返回值
        }


    }

    /**
     * 批量添加资源授权
     * @param domUserOperations
     * @return
     */
    @Override
    public ResultUtils addOps(List<DomUserOperation> domUserOperations) {
        for(DomUserOperation domUserOperation : domUserOperations) {
            DomUserOperation domUserOperation1 = domUserOperationMapper.selectByWhole(domUserOperation);
            if (domUserOperation1 != null) {
                throw new FailedException(ResponseInfo.ALREADYOPERATION_ERROR.getErrorCode(),
                        "添加域" + domUserOperation.getDomId() + "下" + "属主" + domUserOperation.getOwnerId() +
                                "下的此UserOwner" + domUserOperation.getUserOwnerId() + "对资源类型" + domUserOperation.getResTypeId()
                                + "的实例" + domUserOperation.getResId() + "的资源授权失败，因为" +
                                ResponseInfo.ALREADYOPERATION_ERROR.getErrorMsg());
            }
        }
        for(DomUserOperation domUserOperation : domUserOperations){
                domUserOperationMapper.insert(domUserOperation);
            }
        ResultUtils resultUtils = new ResultUtils(ResponseInfo.SUCCESS.getErrorCode(),
                ResponseInfo.SUCCESS.getErrorMsg(),
                domUserOperations.size()
        );
        return resultUtils;
    }

    /**
     *
     * @param domUserOperation
     * @return
     */
    @Override
    public ResultUtils delOp(DomUserOperation domUserOperation) {
        DomUserOperation domUserOperation1 = domUserOperationMapper.selectByWhole(domUserOperation);
        if(domUserOperation1==null){
            throw new FailedException(ResponseInfo.NONOPERATION_ERROR2.getErrorCode(),
                    "删除域"+domUserOperation.getDomId()+"下"+"属主"+domUserOperation.getOwnerId()+
                            "下的此UserOwner"+domUserOperation.getUserOwnerId()+"对资源类型"+domUserOperation.getResTypeId()
                            +"的实例"+domUserOperation.getResId()+"的资源授权失败，因为"+
                            ResponseInfo.NONOPERATION_ERROR2.getErrorMsg());
        }else{
            domUserOperationMapper.delete(domUserOperation1);
            DomUserOperation domUserOperation2 = domUserOperationMapper.selectByWhole(domUserOperation);
            System.out.println("==============domUserOperation2  "+domUserOperation2);
            return  resultUniteServiceImp.resultSuccess(domUserOperation2); //成功返回值
        }
    }

    //检查资源授权--是特定人是否拥有特定资源种类的
    //1 是否可继承  2是否是通用  3是否是自己的权限
    @Override
    public ResultUtils checktOps(DomUserOperation domUserOperation){
        ResultUtils resultUtils = new ResultUtils();
        String resultMessage;
        DomUserOperation domUserOperationTest;

       //直接进行查询，是否有该权限
        domUserOperationTest = domUserOperationMapper.checkOpernationByDomUserOperation(domUserOperation);
       if(domUserOperationTest!=null){
            resultMessage =  "检查资源授权成功，域"+domUserOperation.getDomId()+"下的属主"+
                   domUserOperation.getOwnerId()+"下成员"+domUserOperation.getUserOwnerId()+
                   "有资源类型为"+domUserOperation.getResTypeId()+"，权限为"+domUserOperation.getOpId()+
                   "资源授权记录";
           resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
           resultUtils.setMessage(resultMessage);
           return resultUtils;
       }
       // 检查通用或者继承权限
        if(domUserOperationTest == null){
            //通用性检查
            if(domUserOperationServiceImpl.isExtend(domUserOperation) ) {
                resultMessage = "检查资源授权成功，域" + domUserOperation.getDomId() + "下的属主" +
                        domUserOperation.getOwnerId() + "下成员" + domUserOperation.getUserOwnerId() +
                        "有资源类型为" + domUserOperation.getResTypeId() + "，权限为" + domUserOperation.getOpId() +
                        "资源授权记录";
                resultUtils.setCode(ResponseInfo.SUCCESS_IS.getErrorCode());
                resultUtils.setMessage(resultMessage);
                return resultUtils;
            }

        }



        resultMessage = "检查资源授权失败，域" + domUserOperation.getDomId() + "下的属主" +
                domUserOperation.getOwnerId() + "下成员" + domUserOperation.getUserOwnerId() +
                "不具备资源类型为" + domUserOperation.getResTypeId() + "，权限为" + domUserOperation.getResId() +
                "资源授权";
        resultUtils.setCode(ResponseInfo.OPERATION_NOT.getErrorCode());
        resultUtils.setMessage(resultMessage);
        return resultUtils;
    }

    //首先判断是否通用。再判断是否在上级属组
    public boolean isCommon(DomUserOperation domUserOperation){
        //先检查该资源权限是否在域内是通用的
        Integer domId = domUserOperation.getDomId();
        Integer resTypeId = domUserOperation.getResTypeId();
        Integer ownerId = domUserOperation.getOwnerId();
        Integer type = domUserOperation.getTypes();
        Integer uid = domUserOperation.getUserOwnerId();

        DomResOperation domResOperationTest1 = new DomResOperation();
        domResOperationTest1.setOpId(domUserOperation.getOpId());
        domResOperationTest1.setResTypeId(domUserOperation.getResTypeId());
        domResOperationTest1.setDomId(domUserOperation.getDomId());
        DomResOperation domResOperationCommon = domResOperationMapper.select(domResOperationTest1);
        if(domResOperationCommon == null){
            return false;
        }
        if(domResOperationCommon != null)
        {
            if(domResOperationCommon.getIsCommon() !=1){
                return false;
            }
        }

        //检查属主是否拥有所在域的该资源种类
        //如果参数是用户ID，则进行迭代属主（原始参数的ownerid）
        if(type == 0) {
            do {
                if (domResOperationCommon != null) {
                    if (domOwnerResMapper.selectById(domId, ownerId, resTypeId) != null) {
                        return true;
                    }
                }
                if (domResOperationCommon == null) {
                    ownerId = domResOwnerMapper.selectById(domId, ownerId).getPId();
                }
            } while (ownerId == 0);
        }

        //如果参数是属主id，
        if(type == 1){
            do {
                if (domResOperationCommon != null) {
                    if (domOwnerResMapper.selectById(domId, uid, resTypeId) != null) {
                        return true;
                    }
                }
                if (domResOperationCommon == null) {
                    uid = domResOwnerMapper.selectById(domId, uid).getPId();
                }
            } while (uid == 0);
        }
        //判断ownerId=0等于0的情况
        if(type == 0 && ownerId == 0){
            if (domOwnerResMapper.selectById(domId, ownerId, resTypeId) != null) {
                return true;
            }
        }
            return false;
    }

    //权限继承（在本表中已经不存在，才会检查是否是继承得到的权限）
    //1在资源权限表检查要检查的权限继承种类
    //2权限种类的id是否和父的一样
    //是否拥有父的权限
    public boolean isExtend(DomUserOperation domUserOperation){
        Integer domId = domUserOperation.getDomId();
        Integer resTypeId = domUserOperation.getResTypeId();
        Integer ownerId = domUserOperation.getOwnerId();
        Integer type = domUserOperation.getTypes();
        Integer uid = domUserOperation.getUserOwnerId();
        Integer  opid = domUserOperation.getOpId();

        DomResOperation domResOperationSelect = new DomResOperation();
        domResOperationSelect.setDomId(domId);
        domResOperationSelect.setResTypeId(resTypeId);
        domResOperationSelect.setOpId(opid);

        DomResOperation domResOperationTest = domResOperationMapper.select(domResOperationSelect);
        if(domResOperationTest.getIsExtend() !=1){
            return false;
        }
        if(domResOperationTest.getIsExtend() ==1){
            //表明权限是继承得来的，检查子父权限id是否相同,事实上在设置为继承的时候id一定相同。
            DomResType  domResTypeChild = domResTypeMapper.getDomResTypeByResTypeId(domId,resTypeId);
            DomResType  domResTypeParent = domResTypeMapper.getDomResTypeByResTypeId(domId,domResTypeChild.getPId());
            //接下来检查是否拥有父的这个权限
            DomUserOperation domUserOperationParent= new DomUserOperation();
            domUserOperationParent.setDomId(domId);
            domUserOperationParent.setOwnerId(domResTypeParent.getResTypeId());
            domUserOperationParent.setResTypeId(opid);
            domUserOperationParent.setUserOwnerId(uid);
            domUserOperationParent.setTypes(domUserOperation.getTypes());
            DomUserOperation domUserOperationParentTest;
            domUserOperationParentTest = domUserOperationMapper.checkOpernationByDomUserOperation(domUserOperationParent);
            //检查本表是否存在
            if(domUserOperationParentTest != null){
                return  true;
            }
            //检查是否拥有此资源种类
            if(isCommon(domUserOperationParent)){
                return  true;
            }

        }
        return false;
    }
    }
