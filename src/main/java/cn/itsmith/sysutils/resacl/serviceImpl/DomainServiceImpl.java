package cn.itsmith.sysutils.resacl.serviceImpl;


import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.dao.DomainMapper;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.entities.Domain;
import cn.itsmith.sysutils.resacl.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service(value = "DomainService")
public class DomainServiceImpl implements DomainService {
    @Autowired
    DomainMapper domainMapper;

    @Override
    public Domain verify(int domId, String domToken) {  //根据domid和token认证
        Domain domain = domainMapper.select(domId, domToken);
        if(domain==null){
            throw new FailedException(ResponseInfo.DOMAINAUTH_ERROR);
        }else{
            return domain;
        }

    }

    //wang
    @Override
    public boolean isDomain(Integer domid) {
        Domain isDomain = domainMapper.selectByPrimaryKey(domid);
        if(isDomain == null){
            return false;
        }
        return true;
    }
}
