package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResOperation;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomResOperationService {
    ResultUtils addDomResOperation(DomResOperation domResOperation);
    ResultUtils deleteResOperation(DomResOperation domResOperation);
    ResultUtils getResOperation(int domId, int resTypeId);
    ResultUtils updateOpDes(DomResOperation domResOperation);
    ResultUtils setExtend(DomResOperation domResOperation);
    ResultUtils cancelExtend(DomResOperation domResOperation);
    ResultUtils setCommon(DomResOperation domResOperation);
    ResultUtils cancelCommon(DomResOperation domResOperation);


    boolean resOperationExist(int domId, int resTypeId, int opId);
    boolean opUsing(int domId, int resTypeId, int opId);
}
