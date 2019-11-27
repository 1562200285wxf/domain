package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomUserOperationMapper;
import cn.itsmith.sysutils.resacl.entities.DomUserOperation;

import cn.itsmith.sysutils.resacl.service.DomUserOperationService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value="DomUserOperationService")
public class DomUserOperationServiceImpl implements DomUserOperationService{
    @Autowired
    DomUserOperationMapper domUserOperationMapper;

    ResultUniteServiceImp resultUniteServiceImp = new ResultUniteServiceImp();
    @Override
    public ResultUtils selectOps(DomUserOperation domUserOperation) {

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
            List<DomUserOperation> domUserOperation3 = new ArrayList<DomUserOperation>();
            for (DomUserOperation domUserOperation2:
                    domUserOperation1) {
                if(domUserOperation2.getStatus().equals(1)){
                    domUserOperation3.add(domUserOperation2);
                }
            }
            return  resultUniteServiceImp.resultSuccess(domUserOperation3); //成功返回值
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
}
