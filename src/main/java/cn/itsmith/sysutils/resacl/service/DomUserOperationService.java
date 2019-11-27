package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomUserOperationService {
  
    ResultUtils addOp(DomUserOperation domUserOperation);
    ResultUtils delOp(DomUserOperation domUserOperation);
    ResultUtils selectOps(DomUserOperation domUserOperation);
}
