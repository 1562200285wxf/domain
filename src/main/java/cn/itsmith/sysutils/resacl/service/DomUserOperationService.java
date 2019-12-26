package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

import java.util.List;

public interface DomUserOperationService {
  
    ResultUtils addOp(DomUserOperation domUserOperation);
    ResultUtils addOps(List<DomUserOperation> domUserOperations);
    ResultUtils delOp(DomUserOperation domUserOperation);
    ResultUtils selectOps(DomUserOperation domUserOperation);

    ResultUtils checktOps(DomUserOperation domUserOperation);
}
