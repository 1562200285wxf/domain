package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerResMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomResTypeMapper;
import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.service.DomOwnerResService;
import cn.itsmith.sysutils.resacl.utils.DomResOwnerRNode;

import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "DomOwnerResService")
public class DomOwnerResServiceImpl implements DomOwnerResService {
    @Autowired
    DomOwnerResMapper domOwnerResMapper;

    @Autowired
    DomResOwnerMapper domResOwnerMapper;

    @Autowired
    DomResTypeMapper rTypeMapper;
    /**
     * 获取资源属主树
     * @param domId
     * @param ownerId
     * @return
     */

    @Override
    public ResultUtils getOwnerResTree(int domId, int ownerId){
        //DomResOwnerR domResOwnerR = ownerMapper.selectRById(domId, ownerId);
        DomOwnerRes domOwnerRes = new DomOwnerRes();
        //假头，而数据库中万万没有这一条数据
        domOwnerRes.setDomId(domId);
        domOwnerRes.setOwnerId(ownerId);
        domOwnerRes.setResTypeId(0);//tRid==0本来就代表没用，tRid是从1开始的

        DomResOwnerRNode domResOwnerRNode0 = new DomResOwnerRNode(domOwnerRes);
        //List<DomOwnerRes> domOwnerRess = rTypeMapper.queryResBydomowner(domId, ownerId);
        List<DomOwnerRes> maxOwnerResMaxs = this.getMaxOwnerRes(domId, ownerId);
        for (DomOwnerRes domOwnerRes1:
                maxOwnerResMaxs) {
            DomResOwnerRNode domResOwnerRNodeMax = new DomResOwnerRNode(domOwnerRes1);
            DomResOwnerRNode domResOwnerRNode = createOwnerResTree(domResOwnerRNodeMax);
            domResOwnerRNode0.getChildList().add(domResOwnerRNode);

        }
//        //生成头节点
//        DomResOwnerRNode domResOwnerRNode1 = new DomResOwnerRNode(domResOwnerR);
//        //递归生成子树
//        DomResOwnerRNode domResOwnerRNode = createOwnerResTree(domResOwnerRNode1);
        ResultUtils resultUtils = new ResultUtils(
                ResponseInfo.SUCCESS.getErrorCode(),
                ResponseInfo.SUCCESS.getErrorMsg(),
                domResOwnerRNode0);
//        resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
//        resultUtils.setMessage(ResponseInfo.SUCCESS.getErrorMsg());
//        resultUtils.setData(domResOwnerNode);
        return resultUtils;
    }

    /**
     * 找到maxOwnerRes[]
     * @param domId
     * @param ownerId
     * @return
     */
    public List<DomOwnerRes> getMaxOwnerRes(int domId, int ownerId){
        List<DomOwnerRes> domOwnerRess = rTypeMapper.queryResBydomowner(domId, ownerId);
        if(domOwnerRess.size()==0){
            throw new FailedException(ResponseInfo.NONRES_ERROR3.getErrorCode(),
                    "查询失败，因为域"+domId+"下的属主"+ownerId+"没有拥有任何资源");
        }
        List<DomOwnerRes> list = new ArrayList<DomOwnerRes>();
        for (DomOwnerRes domOwnerRes:
                domOwnerRess) {
            Integer resTypeId = domOwnerRes.getResTypeId();
            System.out.println("======================resTypeId  "+resTypeId);
            DomResType domResType = rTypeMapper.queryResBase(domId, resTypeId);
            System.out.println("======================domResType  "+domResType);
            //if(domResType!=null)为了防止逻辑表有资源类型而基本表没有资源类型【防止手动插入打翻添加逻辑】
            if(domResType!=null){
                Integer pId = domResType.getPId();
                DomOwnerRes domOwnerRes1 = rTypeMapper.queryOwnerRes(domId, ownerId, pId);
                if(domOwnerRes1==null){
                    list.add(domOwnerRes);
                }
            }



        }
        System.out.println("================listlength"+list.size());
        return list;
    }


    /**
     * 递归函数，传入树节点生成一个树，返回树的头节点
     * @param domResOwnerRNode
     * @return
     */
    public DomResOwnerRNode createOwnerResTree(DomResOwnerRNode domResOwnerRNode) {
        Integer domId = domResOwnerRNode.getData().getDomId();
        Integer ownerId = domResOwnerRNode.getData().getOwnerId();
        Integer resTypeId = domResOwnerRNode.getData().getResTypeId();
        List<DomResType> domResTypess = rTypeMapper.queryBaseByPid(domId, resTypeId);
        for (DomResType domResType:
                domResTypess) {
            Integer resTypeId1 = domResType.getResTypeId();
            DomOwnerRes domOwnerRes = rTypeMapper.queryOwnerRes(domId, ownerId, resTypeId1);
            DomResOwnerRNode domResOwnerRNode1 = new DomResOwnerRNode(domOwnerRes);
            domResOwnerRNode.getChildList().add(domResOwnerRNode1);
        }


//        //找到子节点的资源属主
//        List<DomResOwnerR> child = ownerMapper.selectRByPId(domId, ownerId);
//        //将子节点加入节点中
//        for(DomResOwnerR domResOwnerR1 : child){
////            Integer ownerId1 = domResOwnerR1.getOwnerId();
////            Integer domId1 = domResOwnerR1.getDomId();
////            List<DomOwnerRes> resS = rTypeMapper.queryResBydomowner(domId1, ownerId1);
////            if(resS==null){
////                throw new FailedException(ResponseInfo.NONRES_ERROR2.getErrorCode(), ResponseInfo.NONRES_ERROR2.getErrorMsg());
////            }
////            domResOwnerR1.setOwnerRes(resS);
//            DomResOwnerRNode domResOwnerRNode1 = new DomResOwnerRNode(domResOwnerR1);
//
//            domResOwnerRNode.getChildList().add(domResOwnerRNode1);
//        }
        //递归遍历子节点，让每个子节点递归生成子树
        for(cn.itsmith.sysutils.resacl.utils.DomResOwnerRNode domResOwnerRNode1 : domResOwnerRNode.getChildList()){
            if(domResOwnerRNode1.getData() != null){
                createOwnerResTree(domResOwnerRNode1);
            }
        }
        return domResOwnerRNode;
    }

    @Override
    public boolean ownerResExist(int domId, int ownerId, int resTyeId) {
        DomOwnerRes domOwnerRes = domOwnerResMapper.selectById(domId, ownerId, resTyeId);
//        if(domOwnerRes==null){
//            throw new FailedException(ResponseInfo.GETOWNERRES_ERROR.getErrorCode(),
//                    String.format("域标识为%d的域下的标识为%d的属主不存在标识为%d的资源种类",
//                            domId, ownerId, resTyeId));
//        }else {
        if(domOwnerRes==null || domOwnerRes.getStatus()==0)
            return false;
        else
            return true;
//        }

    }
}
