package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.entities.DomResOwner;

import cn.itsmith.sysutils.resacl.entities.DomResOwnerRMax;
import cn.itsmith.sysutils.resacl.entities.DomResType;

import cn.itsmith.sysutils.resacl.service.DomOwnerResMaxService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value="DomOwnerResMaxService")
public class DomOwnerResMaxServiceImp implements DomOwnerResMaxService {
    @Autowired
    DomResOwnerMapper ownerMapper;
    @Autowired
    DomResTypeMapper rTypeMapper;
    @Override
    public ResultUtils getOwnerMaxRess(int domId, int ownerId) {
        DomResOwner domResOwner = ownerMapper.queryOwnerBydomowner(domId, ownerId);
        //带list<>不能查走mapper只能new出来
        DomResOwnerRMax domResOwnerRMax = new DomResOwnerRMax(domResOwner);
//        domResOwnerRMax.setDomId(domId);
//        domResOwnerRMax.setOwnerId(ownerId);
        List<DomOwnerRes> domOwnerRess = rTypeMapper.queryResBydomowner(domId, ownerId);
        System.out.println("11111111111111111111111111111111"+domOwnerRess);
        if(domOwnerRess.size()==0){
            throw new FailedException(ResponseInfo.NONRES_ERROR3.getErrorCode(),
                    "查询失败，因为域"+domId+"下的属主"+ownerId+"没有拥有任何资源");
        }else{
            for (DomOwnerRes domOwnerRes:domOwnerRess) {
                Integer resTypeId = domOwnerRes.getResTypeId();
                //System.out.println("-----------------------------resTypeId"+resTypeId);
                DomResType domResType = rTypeMapper.queryResBase(domId, resTypeId);
                if(domResType==null){
                    throw new FailedException(444,
                            "查询失败，因为域"+domId+"下的属主"+ownerId+"拥有基本表不存在的资源类型");
                }
                Integer rTPId = domResType.getPId();
                System.out.println("00000000000000000000000rpId"+rTPId);
                if(rTPId==0){
                    System.out.println("2222222222222222222222222"+domResOwnerRMax);
                    System.out.println("3333333333333333333333333"+domResOwnerRMax.getDomResTypes());
                    domResOwnerRMax.getDomResTypes().add(domResType);
                }else{
                    //以基本表查到的rTPId作为resTypeId条件查询逻辑表中是否存在对象
                    DomOwnerRes domOwnerRes1 = rTypeMapper.queryOwnerRes(domId, ownerId, rTPId);
                    if(domOwnerRes1==null){
                        domResOwnerRMax.getDomResTypes().add(domResType);
                    }
                }
            }
            ResultUtils resultUtils = new ResultUtils(
                    ResponseInfo.SUCCESS.getErrorCode(),
                    ResponseInfo.SUCCESS.getErrorMsg(),
                    domResOwnerRMax);
            return resultUtils;
        }


    }
}
