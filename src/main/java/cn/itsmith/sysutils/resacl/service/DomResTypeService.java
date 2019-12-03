package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResType;
import cn.itsmith.sysutils.resacl.utils.ResultUtils;

import java.util.List;

public interface DomResTypeService {
    boolean domResTypeExist(int domId, int resTypeId);

    //wang
    //实现添加，删除，修改的逻辑操作的原子性，返回操作正确与否结果，具体的权限之类的不在这进行判断。
    public ResultUtils addResourceType(DomResType domResType);
    public ResultUtils changeResourceTypeDes(DomResType domResType);
    public ResultUtils deleteResourceType(DomResType domResType);
    public ResultUtils getDomResTree(Integer domid,Integer resTypeId);
}
