package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerUserMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.entities.*;
import cn.itsmith.sysutils.resacl.service.DomOwnerUserService;
import cn.itsmith.sysutils.resacl.utils.DomResOwnerUNode;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value="DomOwnerUserService")
public class DomOwnerUserServiceImpl implements DomOwnerUserService {

    @Autowired
    DomOwnerUserMapper domOwnerUserMapper;
    @Autowired
    DomResOwnerMapper ownerMapper;

    @Override
    public boolean userExist(int domId, int ownerId, int userId) {
        DomOwnerUser domOwnerUser = domOwnerUserMapper.selectById(domId, ownerId, userId);
//        if(domOwnerUser==null){
//            throw new FailedException(ResponseInfo.GETUSER_ERROR.getErrorCode(),
//                    String.format("域标识为%d的域下的标识为%d的属主不存在标识为%d的用户",
//                    domId, ownerId, userId));
//        }else {
        if(domOwnerUser==null || domOwnerUser.getStatus()==0)
            return false;
        else
            return true;
//        }
    }

    //liu
    static int c=0;

    /**
     * 获取资源属主树
     * @param domId
     * @param ownerId
     * @return
     */
    @Override
    public ResultUtils getOwnerUserTree(int domId, int ownerId){
        DomResOwnerUU domResOwnerUU = ownerMapper.selectUById(domId, ownerId);
        //至于这里判断属主存在，是为了防止空指针异常
        if(domResOwnerUU ==null||domResOwnerUU.getStatus().equals(0)){
            throw new FailedException(1002,"属主"+domResOwnerUU.getOwnerId()+"不存在");
        }

        DomResOwnerUU domResOwnerUU0 = new DomResOwnerUU();
        domResOwnerUU0.setDomId(0);
        DomResOwnerUNode domResOwnerUNode0 = new DomResOwnerUNode(domResOwnerUU0);//假头

        List<DomOwnerUser> users = domOwnerUserMapper.queryUserBydomowner(domId, ownerId);
        for (DomOwnerUser domOwnerUser:
        users) {
            Integer userId = domOwnerUser.getUserId();
            String userName = domOwnerUserMapper.queryUserName(userId);
            if(userName!=null){
                domOwnerUser.setUserName(userName);
            }


        }
//        if(users==null){
//            throw new FailedException(ResponseInfo.NONUSER_ERROR3.getErrorCode(), ResponseInfo.NONUSER_ERROR3.getErrorMsg());
//        }

        domResOwnerUU.setOwnerUsers(users);
        //生成头节点
        DomResOwnerUNode domResOwnerNode1 = new DomResOwnerUNode(domResOwnerUU);
        //递归生成子树
        int countReal = 0;
        DomResOwnerUNode domResOwnerNode = createOwnerResTree(domResOwnerNode1);
        //加入假头
        domResOwnerUNode0.getChildList().add(domResOwnerNode);
        //
        List<DomResOwnerUNode> childList = domResOwnerNode.getChildList();
        if(childList!=null){
            for (DomResOwnerUNode domResOwnerUNode:
                    childList) {
                countReal++;
            }

        }
        System.out.println("------------------countReal="+countReal);

        ResultUtils resultUtils = new ResultUtils(
                ResponseInfo.SUCCESS.getErrorCode(),
                ResponseInfo.SUCCESS.getErrorMsg(),
                domResOwnerUNode0);
//        resultUtils.setCode(ResponseInfo.SUCCESS.getErrorCode());
//        resultUtils.setMessage(ResponseInfo.SUCCESS.getErrorMsg());
//        resultUtils.setData(domResOwnerNode);
        return resultUtils;
    }



    /**
     * 递归函数，传入树节点生成一个树，返回树的头节点
     * @param domResOwnerUNode
     * @return
     */
    public DomResOwnerUNode createOwnerResTree(DomResOwnerUNode domResOwnerUNode) {

        int domId = domResOwnerUNode.getData().getDomId();
        int ownerId = domResOwnerUNode.getData().getOwnerId();



        //找到子节点的资源属主
        List<DomResOwnerUU> child = ownerMapper.selectUByPId(domId, ownerId);
        //将子节点加入节点中
        for(DomResOwnerUU domResOwnerUU1 : child){
            Integer ownerId1 = domResOwnerUU1.getOwnerId();
            Integer domId1 = domResOwnerUU1.getDomId();
            List<DomOwnerUser> users =  domOwnerUserMapper.queryUserBydomowner(domId1, ownerId1);
//            if(users==null){
//                throw new FailedException(ResponseInfo.NONUSER_ERROR3.getErrorCode(), ResponseInfo.NONUSER_ERROR3.getErrorMsg());
//            }

            for (DomOwnerUser domOwnerUser:
                    users) {
                Integer userId = domOwnerUser.getUserId();
                String userName = domOwnerUserMapper.queryUserName(userId);
                System.out.println("+++++++++++userName"+userName);
                if(userName!=null){
                    domOwnerUser.setUserName(userName);
                }


            }
            domResOwnerUU1.setOwnerUsers(users);




            DomResOwnerUNode domResOwnerUNode1 = new DomResOwnerUNode(domResOwnerUU1);

            domResOwnerUNode.getChildList().add(domResOwnerUNode1);


        }
        //递归遍历子节点，让每个子节点递归生成子树
        for(DomResOwnerUNode domResOwnerNode1 : domResOwnerUNode.getChildList()){
            if(domResOwnerNode1.getData() != null){

                createOwnerResTree(domResOwnerNode1);
            }
        }
        return domResOwnerUNode;
    }

}


