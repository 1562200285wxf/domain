package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.Domain;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

public interface DomainService {
    Domain verify(int domId, String token);
    //wang
    public boolean isDomain(Integer domid);

    public ResultUtils addDomain(Domain domain);

}
