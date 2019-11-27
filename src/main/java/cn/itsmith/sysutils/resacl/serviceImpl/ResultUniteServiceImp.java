package cn.itsmith.sysutils.resacl.serviceImpl;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import cn.itsmith.sysutils.resacl.service.ResultUniteService;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;
import io.swagger.annotations.ApiModel;

import java.util.List;

public class ResultUniteServiceImp implements ResultUniteService {
    @Override
    public ResultUtils resultSuccess(Object data) {
        String susses=null;
        if(data.getClass().equals(DomOwnerUser.class)){
            susses  = "susses 操作成功后的返回给你的对象数据类型是【域中的属主成员】";
        }else if(data.getClass().equals(DomOwnerRes.class)){
            susses  = "susses 操作成功后的返回给你的对象数据类型是【域中的属主拥有的资源】";
        }else if(data.getClass().equals(DomUserOperation.class)) {
            susses  = "susses 操作成功后的返回给你的对象数据类型是【域中的资源授权】";
        }else{
            susses  = "susses 操作成功后的返回给你的对象数据类型是【List】";
        }
        ResultUtils resultUtils = new ResultUtils(200,susses,data);
        return resultUtils;
    }
}
