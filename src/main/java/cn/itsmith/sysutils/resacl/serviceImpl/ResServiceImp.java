package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomResInstanceMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.entities.*;

import cn.itsmith.sysutils.resacl.service.DomResInstanceService;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.service.ResService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service(value="ResService")
public class ResServiceImp implements ResService {
    @Autowired
    DomResTypeMapper rTypeMapper;
    @Autowired
    DomResOwnerMapper ownerMapper;
    @Autowired
    DomResOwnerService domResOwnerService1;
    @Autowired
    DomResInstanceService domResInstanceService;
    ResultUniteServiceImp resultUniteServiceImp = new ResultUniteServiceImp();
    @Override
    public ResultUtils addRes(DomOwnerRes domOwnerRes) {
//        List<Integer> listOwnerIds = ownerMapper.queryAllOwnerId(domOwnerRes.getDomId());//基本属主不存在
        DomOwnerRes domOwnerRes1 = rTypeMapper.queryOwnerRes(domOwnerRes.getDomId(), domOwnerRes.getOwnerId(), domOwnerRes.getResTypeId());
        List<Integer> listResIds2= rTypeMapper.queryAllResId2(domOwnerRes.getDomId());//本来就没有种类type,【rtype仍然是个逻辑表】要传入domid
        boolean hasOwner = false;
        boolean hasResInOwner  =false;
        boolean hasRes=false;
//        for (Integer id : listOwnerIds) {
//            if(domOwnerRes.getOwnerId()==id){
//                hasOwner=true;
//                break;
//            }
//        }

        if(domOwnerRes1!=null&&domOwnerRes1.getStatus().equals(1)){
            hasResInOwner=true;
        }
        for (Integer id : listResIds2) {
            if(domOwnerRes.getResTypeId()==id){
                hasRes=true;
                break;
            }
        }
        if(!(domResOwnerService1.ownerExist(domOwnerRes.getDomId(),domOwnerRes.getOwnerId()))){
            throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(),
                    "不能添加资源种类"+domOwnerRes.getResTypeId()+
                            "到域"+domOwnerRes.getDomId()+
                            "下的属主"+domOwnerRes.getOwnerId()+
                            "下，因为域"+domOwnerRes.getDomId()+"下没有属主"+domOwnerRes.getOwnerId());
        }else if(hasResInOwner){
            throw new FailedException(ResponseInfo.DUMPILCATERES_ERROR.getErrorCode(),
                    "不能添加资源种类"+domOwnerRes.getResTypeId()+
                            "，因为域"+domOwnerRes.getDomId()+
                            "属主"+domOwnerRes.getOwnerId()+"下已经存在"+"资源种类"+domOwnerRes.getResTypeId()
            );
        }else if(!hasRes){
            throw new FailedException(ResponseInfo.NONRES_ERROR.getErrorCode(),
                    "不能添加资源种类"+domOwnerRes.getResTypeId()+
                            "，因为资源种类"+domOwnerRes.getResTypeId()+
                            "不存在于基本表`t_dom_res_type`，同样不能添加"
            );
        }
        else{
            ResultUtils resultUtils = new ResultUtils(
                    ResponseInfo.SUCCESS.getErrorCode(),
                    ResponseInfo.SUCCESS.getErrorMsg()+"!!,资源种类"+domOwnerRes.getResTypeId()+"已经成功被添加进domOwnerRes表中");
            if(domOwnerRes1==null){
                rTypeMapper.addOwnerRes(domOwnerRes);//添加的是用户输的domOwnerRes

                resultUtils.setData(domOwnerRes.getId());
            }else if(domOwnerRes1!=null&domOwnerRes1.getStatus().equals(0)){
                //更新status=1
                rTypeMapper.updataStatus(domOwnerRes1);//更新的已经查出来的domOwnerRes1
                resultUtils.setData(domOwnerRes1.getId());
            }

            return resultUtils;
            //return  resultUniteServiceImp.resultSuccess(domOwnerRes);
        }
    }

    @Autowired
    DomResInstanceMapper resInstanceMapper;
    @Override
    public ResultUtils delRes(DomOwnerRes domOwnerRes) {
        Integer domId = domOwnerRes.getDomId();
        Integer ownerId = domOwnerRes.getOwnerId();
        Integer resTypeId = domOwnerRes.getResTypeId();
        List<DomResInstance> domResInstances= resInstanceMapper.beingUsed(domId,ownerId,resTypeId);//ResInstance没使用这个类型才能删除，即返回值=0才行,可能为1/2/3
        boolean isBingUsed = false;
        if(domResInstances!=null){
            for (DomResInstance domResInstance:
                    domResInstances) {
                Integer status = domResInstance.getStatus();
                if(status.equals(1)){
                    isBingUsed=true;
                }
            }
        }

        DomOwnerRes domOwnerRes2 = rTypeMapper.queryOwnerRes(domId, ownerId, resTypeId);
        boolean hasResInOwnerRes  =false;
        Integer j = domOwnerRes.getResTypeId();

        if(domOwnerRes2!=null&&domOwnerRes2.getStatus().equals(1)){
            hasResInOwnerRes=true;
        }
        if(!hasResInOwnerRes){
            throw new FailedException(ResponseInfo.NONRES_ERROR2.getErrorCode(),
                    "不能删除属主拥有的资源类型"+domOwnerRes.getResTypeId()+
                            "，因为属主拥有的资源类型"+domOwnerRes.getResTypeId()+
                            "不存在于逻辑表`t_dom_owner_res`，先添加才能删除");
        }else if(isBingUsed){
            throw new FailedException(ResponseInfo.BEINGUSED_ERROR.getErrorCode(),
                    "不能删除属主拥有的资源类型"+domOwnerRes.getResTypeId()+
                            "，因为属主拥有的资源类型"+domOwnerRes.getResTypeId()+
                    "正在被资源实例使用");
        }
        else{

            rTypeMapper.deleteType(
                    domOwnerRes.getDomId(),
                    domOwnerRes.getOwnerId(),
                    domOwnerRes.getResTypeId());//set status==0
            DomOwnerRes domOwnerRes1 = rTypeMapper.queryOwnerRes(
                    domOwnerRes.getDomId(),
                    domOwnerRes.getOwnerId(),
                    domOwnerRes.getResTypeId());
            return  resultUniteServiceImp.resultSuccess(domOwnerRes1);
        }
    }

    /**
     * 从基本表中查询特定属主下不存在的资源类型
     * @param domId
     * @param ownerId
     * @return
     */
    @Override
    public ResultUtils getResWithoutOwner(Integer domId, Integer ownerId) {
        List<DomOwnerRes> domOwnerRes = rTypeMapper.queryResBydomowner(domId, ownerId);
        List<DomResType> domResTypes = rTypeMapper.queryResBases(domId);
        if(domOwnerRes.size()!=0){

            Iterator<DomResType> it = domResTypes.iterator();
            while(it.hasNext()) {
                DomResType domResType = it.next();
                for (DomOwnerRes domOwnerRes1:
                        domOwnerRes) {
                    if(domResType.getResTypeId().equals(domOwnerRes1.getResTypeId())){
                        it.remove();
                        break;
                    }
                }
            }
            return resultUniteServiceImp.resultSuccess(domResTypes);
        }else{
            return resultUniteServiceImp.resultSuccess(domResTypes);
        }

    }
}
