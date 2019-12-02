package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.common.config.ResponseInfo;
import cn.itsmith.sysutils.resacl.common.exception.FailedException;
import cn.itsmith.sysutils.resacl.dao.DomOwnerUserMapper;
import cn.itsmith.sysutils.resacl.dao.DomResOwnerMapper;
import cn.itsmith.sysutils.resacl.dao.DomUserOperationMapper;
import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import cn.itsmith.sysutils.resacl.entities.User;
import cn.itsmith.sysutils.resacl.service.DomResOwnerService;
import cn.itsmith.sysutils.resacl.service.UserService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service(value="UserService")
public class UserServiceImp implements UserService {
    @Autowired
    DomOwnerUserMapper userMapper;

    @Autowired
    DomUserOperationMapper domUserOperationMapper;
    @Autowired
    DomResOwnerMapper ownerMapper1;
    @Autowired
    DomResOwnerService domResOwnerService1;


    ResultUniteServiceImp resultUniteServiceImp = new ResultUniteServiceImp();

    @Override
    public ResultUtils addUser(DomOwnerUser domOwnerUser) {


        List<Integer> listUserIds2 = userMapper.queryAlluserId2();//本来就没有用户user
        DomOwnerUser domOwnerUser1 = userMapper.queryUser(domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());
        boolean hasUserInDom = false;
        boolean hasUser = false;


        if (domOwnerUser1 != null && domOwnerUser1.getStatus().equals(1)) {
            hasUserInDom = true;
        }
        for (Integer id : listUserIds2) {
            if (domOwnerUser.getUserId() == id) {
                hasUser = true;
                break;
            }
        }
        if (!(domResOwnerService1.ownerExist(domOwnerUser.getDomId(), domOwnerUser.getOwnerId()))) {
            throw new FailedException(ResponseInfo.OWNER_FAILED.getErrorCode(),
                    "不能添加成员" + domOwnerUser.getUserId() +
                            "到域" + domOwnerUser.getDomId() +
                            "下属主" + domOwnerUser.getOwnerId() +
                            "下，因为域" + domOwnerUser.getDomId() + "下没有属主" + domOwnerUser.getOwnerId());
        } else if (hasUserInDom) {
            throw new FailedException(ResponseInfo.DUMPILCATE_ERROR.getErrorCode(),
                    "不能添加成员" + domOwnerUser.getUserId() +
                            "，因为域" + domOwnerUser.getDomId() +
                            "属主" + domOwnerUser.getOwnerId() + "下已经存在" + "成员" + domOwnerUser.getUserId());
        } else if (!hasUser) {
            throw new FailedException(ResponseInfo.NONUSER_ERROR.getErrorCode(),
                    "不能添加成员" + domOwnerUser.getUserId() +
                            "，因为成员" + domOwnerUser.getUserId() +
                            "不存在于基本表，同样不能添加"
            );
        } else {
            ResultUtils resultUtils = new ResultUtils(ResponseInfo.SUCCESS.getErrorCode(),
                    ResponseInfo.SUCCESS.getErrorMsg() + "!!,成员" + domOwnerUser.getUserId() + "已经成功被添加进domOwnerUser表中");
            if (domOwnerUser1 == null) {
                userMapper.addDomUser(domOwnerUser);
                resultUtils.setData(domOwnerUser.getId());
                return resultUtils;
                //return  resultUniteServiceImp.resultSuccess(domOwnerUser);
            } else if (domOwnerUser1 != null && domOwnerUser1.getStatus().equals(0)) {
                //更新status=1
                userMapper.updataStatus(domOwnerUser1);
                resultUtils.setData(domOwnerUser1.getId());

            }
            return resultUtils;
        }


    }

    @Override
    public ResultUtils delUser(DomOwnerUser domOwnerUser) {
        List<Integer> listUserIds = userMapper.queryAlluserIdsByDomId(domOwnerUser.getDomId(), domOwnerUser.getOwnerId());//用户存在于已经输入的dom，owner才能删除
        boolean hasUserInDom = false;
        for (Integer id : listUserIds) {
            DomOwnerUser domOwnerUser1 = userMapper.queryUser(domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), id);
            if (domOwnerUser.getUserId().equals(id) && domOwnerUser1.getStatus().equals(1)) {
                hasUserInDom = true;
                break;
            }
        }

        if (!hasUserInDom) {
            throw new FailedException(ResponseInfo.NONUSER_ERROR2.getErrorCode(),
                    "删除失败！！，成员" + domOwnerUser.getUserId() + "不存在于你所输入的域" + domOwnerUser.getDomId() +
                            "属主" + domOwnerUser.getOwnerId() + "下，不能删除"
            );
        } else {

            System.out.print("===================================" + domOwnerUser.getDomId());
            userMapper.deleteUser(
                    domOwnerUser.getDomId(),
                    domOwnerUser.getOwnerId(),
                    domOwnerUser.getUserId());
            DomOwnerUser domOwnerUser1 = userMapper.queryUser(
                    domOwnerUser.getDomId(),
                    domOwnerUser.getOwnerId(),
                    domOwnerUser.getUserId()
            );
            ////删除成员的同时删除资源授权
            List<DomUserOperation> domUserOperations = domUserOperationMapper.selectByuId(
                    domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());
            if (domUserOperations != null) {
                domUserOperationMapper.deleteByuId(
                        domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());
            }
            return resultUniteServiceImp.resultSuccess(domOwnerUser1);

        }
    }

    @Override
    public ResultUtils setAdmin(DomOwnerUser domOwnerUser) {
        //更新是添加后的操作，所以不必单独检查基本属主是否存在，只需检查下面的一整条记录【by(domId,ownerId,userId)】在不在即可
        DomOwnerUser domOwnerUser2 = userMapper.queryUser(domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());


        if (domOwnerUser2 == null || domOwnerUser2.getStatus().equals(0)) {
            throw new FailedException(ResponseInfo.NONUSER_ERROR3.getErrorCode(),
                    "不能设置域" + domOwnerUser.getDomId() + "属主" + domOwnerUser.getOwnerId() + "下的成员" +
                            domOwnerUser.getUserId() + "为管理员，因为" +
                            ResponseInfo.NONUSER_ERROR3.getErrorMsg());
        } else if (domOwnerUser2.getIsAdmin() == 1) {
            throw new FailedException(1010,
                    "不能设置域" + domOwnerUser.getDomId() + "属主" + domOwnerUser.getOwnerId() + "下的成员" +
                            domOwnerUser.getUserId() +
                            "为管理员，因为它已经是管理员了不能重复赋予");
        } else {
            //
            userMapper.setAdmin(domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());
            //于删除不一样的是更新操作应该将更新后的对象返回而不是更新前
            DomOwnerUser domOwnerUser1 = userMapper.queryUser(
                    domOwnerUser.getDomId(),
                    domOwnerUser.getOwnerId(),
                    domOwnerUser.getUserId()
            );
            return resultUniteServiceImp.resultSuccess(domOwnerUser1);
        }

    }

    @Override
    public ResultUtils concelAdmin(DomOwnerUser domOwnerUser) {
        DomOwnerUser domOwnerUser2 = userMapper.queryUser(domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());

        if (domOwnerUser2 == null || domOwnerUser2.getStatus().equals(0)) {
            throw new FailedException(ResponseInfo.NONUSER_ERROR4.getErrorCode(),
                    "不能取消域" + domOwnerUser.getDomId() + "属主" + domOwnerUser.getOwnerId() + "下的成员" +
                            domOwnerUser.getUserId() +
                            "的管理员身份，因为" +
                            ResponseInfo.NONUSER_ERROR4.getErrorMsg());
        } else if (domOwnerUser2.getIsAdmin() == 0) {
            throw new FailedException(1010,
                    "不能取消域" + domOwnerUser.getDomId() + "属主" + domOwnerUser.getOwnerId() + "下的成员" +
                            domOwnerUser.getUserId() +
                            "的管理员身份，因为因为它本来就不是管理员，不能重复取消");
        } else {
            userMapper.cancelAdmin(domOwnerUser.getDomId(), domOwnerUser.getOwnerId(), domOwnerUser.getUserId());
            DomOwnerUser domOwnerUser1 = userMapper.queryUser
                    (domOwnerUser.getDomId(),
                            domOwnerUser.getOwnerId(),
                            domOwnerUser.getUserId());
            return resultUniteServiceImp.resultSuccess(domOwnerUser1);
        }

    }

    @Override
    public ResultUtils queryUsers(DomOwnerUser domOwnerUser) {
        Integer domId = domOwnerUser.getDomId();
        Integer ownerId = domOwnerUser.getOwnerId();
        List<DomOwnerUser> users = userMapper.queryUserBydomowner(domId, ownerId);
        if (users != null) {
            for (DomOwnerUser user :
                    users) {
                Integer userId = user.getUserId();
                String userName = userMapper.queryUserName(userId);
                user.setUserName(userName);
            }
            return resultUniteServiceImp.resultSuccess(users);
        } else {
            throw new FailedException(999,
                    "成员不存在"
            );
        }

    }

    @Override
    public ResultUtils queryBaseUsers(DomOwnerUser domOwnerUser) {
        List<User> allBase = userMapper.getAllBase();
        List<DomOwnerUser> allDomUsers = userMapper.queryUserBydomowner(domOwnerUser.getDomId(), domOwnerUser.getOwnerId());
        //ArrayList<User> temp = new ArrayList<>();
//待删的集合一定要用迭代器，不然报错
        Iterator<User> it = allBase.iterator();
        while(it.hasNext()) {
            for (DomOwnerUser domOwnerUser1:
                    allDomUsers) {
                if(it.next().getUserId().equals(domOwnerUser1.getUserId())){
                    allBase.remove(it.next());
                }
            }
            }




         return resultUniteServiceImp.resultSuccess(allBase);
    }
}
