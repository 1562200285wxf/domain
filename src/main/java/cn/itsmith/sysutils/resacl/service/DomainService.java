package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.Domain;

public interface DomainService {
    Domain verify(int domId, String token);
    //wang
    public boolean isDomain(Integer domid);
}
