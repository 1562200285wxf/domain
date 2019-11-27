package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//属主拥有资源。主要是域下属主拥有哪种类型资源，还没有添加权限
@Data
public class DomOwnerRes {
    Integer id;
    Integer domId;
    Integer resTypeId;
    Integer ownerId;
    Integer status;
}
