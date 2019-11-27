package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomResOperationMapper;
import cn.itsmith.sysutils.resacl.dao.DomUserOperationMapper;
import cn.itsmith.sysutils.resacl.entities.DomResOperation;
import cn.itsmith.sysutils.resacl.service.DomResOperationService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "DomResOperationService")
public class DomResOperationServiceImpl implements DomResOperationService {

    @Autowired
    DomResOperationMapper domResOperationMapper;
    @Autowired
    DomUserOperationMapper domUserOperationMapper;

    /**
     * 添加资源可用权限
     * @param domResOperation
     * @return
     */
    @Override
    public ResultUtils addDomResOperation(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        domResOperation.setStatus(1);
        if(domResOperationMapper.insert(domResOperation)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功为标识为%d的域下标识为%d的资源种类添加标识为%d的资源可用权限",
                    domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
            resultUtils.setData(domResOperation.getOpId());
        }else {
            throw new FailedException(String.format("未知错误，为标识为%d的域下标识为%d的资源种类添加资源可用权限失败",
                    domResOperation.getDomId(), domResOperation.getResTypeId()));
        }
        return resultUtils;
    }

    /**
     * 删除资源可用权限
     * @param domResOperation
     * @return
     */
    @Override
    public ResultUtils deleteResOperation(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        domResOperation.setStatus(0);
        if (domResOperationMapper.updateStatus(domResOperation) == 1) {
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功移除标识为%d的域下标识为%d的资源种类添加标识为%d的资源可用权限",
                    domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
            // resultUtils.setData(domResOwner);
        } else {
            throw new FailedException(String.format("未知错误，移除标识为%d的域下标识为%d的资源种类的标识为%d的资源可用权限失败",
                    domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
        }
        return resultUtils;
    }

    /**
     * 查询资源可用权限
     * @param domId
     * @param resTypeId
     * @return
     */
    @Override
    public ResultUtils getResOperation(int domId, int resTypeId) {
        ResultUtils resultUtils = new ResultUtils();
        List<DomResOperation> domResOperations = domResOperationMapper.selectById(domId, resTypeId);
        List<DomResOperation> domResOperations1 = new ArrayList<DomResOperation>();
        for(DomResOperation domResOperation : domResOperations){
            if(domResOperation.getStatus()==1)
                domResOperations1.add(domResOperation);
        }
        if(domResOperations.size()!=0){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功获取标识为%d下的标识为%d的资源种类的可用权限",
                    domId, resTypeId));
            resultUtils.setData(domResOperations1);
        }else{
            throw new FailedException("数据查找失败");
        }
        return resultUtils;
    }

    @Override
    public ResultUtils updateOpDes(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        if(domResOperationMapper.updateOpDes(domResOperation)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改标识为%d的域下标识为%d的资源种类的标识为%d的可用权限的描述为%s",
                    domResOperation.getDomId(), domResOperation.getResTypeId(),
                    domResOperation.getOpId(), domResOperation.getOpDes()));
            domResOperation = domResOperationMapper.select(domResOperation);
            resultUtils.setData(domResOperation);
        }
        return resultUtils;
    }

    @Override
    public ResultUtils setExtend(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        DomResOperation domResOperation1 = domResOperationMapper.select(domResOperation);
        if(domResOperation1.getIsExtend()==1)
            throw new FailedException(ResponseInfo.OPEXTEND1_ERROR.getErrorCode(),
                    String.format("标识为%d的域下标识为%d的资源种类的标识为%d的资源可用权限已经是可继承",
                    domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
        domResOperation.setIsExtend(1);
        if(domResOperationMapper.updateIsExtend(domResOperation)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改标识为%d的域下标识为%d的资源种类的标识为%d的可用权限为可继承",
                    domResOperation.getDomId(), domResOperation.getResTypeId(),
                    domResOperation.getOpId()));
            domResOperation = domResOperationMapper.select(domResOperation);
            resultUtils.setData(domResOperation);
        }
        return resultUtils;
    }

    @Override
    public ResultUtils cancelExtend(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        DomResOperation domResOperation1 = domResOperationMapper.select(domResOperation);
        if(domResOperation1.getIsExtend()==0)
            throw new FailedException(ResponseInfo.OPEXTEND2_ERROR.getErrorCode(),
                    String.format("标识为%d的域下标识为%d的资源种类的标识为%d的资源可用权限已经是不可继承",
                            domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
        domResOperation.setIsExtend(0);
        if(domResOperationMapper.updateIsExtend(domResOperation)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改标识为%d的域下标识为%d的资源种类的标识为%d的可用权限为不可继承",
                    domResOperation.getDomId(), domResOperation.getResTypeId(),
                    domResOperation.getOpId()));
            domResOperation = domResOperationMapper.select(domResOperation);
            resultUtils.setData(domResOperation);
        }
        return resultUtils;
    }

    @Override
    public ResultUtils setCommon(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        DomResOperation domResOperation1 = domResOperationMapper.select(domResOperation);
        if(domResOperation1.getIsCommon()==1)
            throw new FailedException(ResponseInfo.OPCOMMON1_ERROR.getErrorCode(),
                    String.format("标识为%d的域下标识为%d的资源种类的标识为%d的资源可用权限已经是通用",
                            domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
        domResOperation.setIsCommon(1);
        if(domResOperationMapper.updateIsCommon(domResOperation)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改标识为%d的域下标识为%d的资源种类的标识为%d的可用权限为通用",
                    domResOperation.getDomId(), domResOperation.getResTypeId(),
                    domResOperation.getOpId()));
            domResOperation = domResOperationMapper.select(domResOperation);
            resultUtils.setData(domResOperation);
        }
        return resultUtils;
    }

    @Override
    public ResultUtils cancelCommon(DomResOperation domResOperation) {
        ResultUtils resultUtils = new ResultUtils();
        DomResOperation domResOperation1 = domResOperationMapper.select(domResOperation);
        if(domResOperation1.getIsCommon()==0)
            throw new FailedException(ResponseInfo.OPCOMMON2_ERROR.getErrorCode(),
                    String.format("标识为%d的域下标识为%d的资源种类的标识为%d的资源可用权限已经是不可通用",
                            domResOperation.getDomId(), domResOperation.getResTypeId(), domResOperation.getOpId()));
        domResOperation.setIsCommon(0);
        if(domResOperationMapper.updateIsCommon(domResOperation)==1){
            resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
            resultUtils.setMessage(String.format("成功修改标识为%d的域下标识为%d的资源种类的标识为%d的可用权限为不可通用",
                    domResOperation.getDomId(), domResOperation.getResTypeId(),
                    domResOperation.getOpId()));
            domResOperation = domResOperationMapper.select(domResOperation);
            resultUtils.setData(domResOperation);
        }
        return resultUtils;
    }

    /**
     * 查询资源可用权限是否存在
     * @param
     * @return
     */
    @Override
    public boolean resOperationExist(int domId, int resTypeId, int opId) {
        DomResOperation domResOperation = new DomResOperation();
        domResOperation.setDomId(domId);
        domResOperation.setResTypeId(resTypeId);
        domResOperation.setOpId(opId);
        DomResOperation domResOperation1 = domResOperationMapper.select(domResOperation);
//        if(domResOperation1 == null){
//            throw new FailedException(ResponseInfo.GETRESOPERATION_ERROR.getErrorCode(),
//                    String.format("不存在标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限",
//                            domId, resTypeId, opId));
//        }else{
        if(domResOperation1==null || domResOperation1.getStatus()==0)
            return false;
        else
            return true;
//        }
    }

    @Override
    public boolean opUsing(int domId, int resTypeId, int opId) {

        if(domUserOperationMapper.selectByOpId(domId, resTypeId, opId).size() != 0)
            throw new FailedException(ResponseInfo.OPUSING_ERROR.getErrorCode(),
                    String.format("标识为%d的域下的标识为%d的资源种类的标识为%d的资源可用权限已经授权，不能删除",
                            domId, resTypeId ,opId));
        return false;
    }
}
