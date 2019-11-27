package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//资源种类：划分资源整体。
@Data
public class DomResType {
    Integer domId;
    Integer resTypeId;
    String resTypeDes;
    Integer pId;
    String resName;
    Integer status;
}
