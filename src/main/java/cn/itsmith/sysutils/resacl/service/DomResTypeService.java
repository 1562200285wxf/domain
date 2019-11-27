package cn.itsmith.sysutils.resacl.service;

import cn.itsmith.sysutils.resacl.entities.DomResType;

import java.util.List;

public interface DomResTypeService {
    boolean domResTypeExist(int domId, int resTypeId);

    //wang
    //实现添加，删除，修改的逻辑操作的原子性，返回操作正确与否结果，具体的权限之类的不在这进行判断。
    public String addResourceType(DomResType domResType);
    public String changeResourceTypeDes(DomResType domResType);
    public String deleteResourceType(DomResType domResType);
    public List<DomResType> getDomResTypes(Integer domid);
}
