package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

@Data
public class DomResOperationU {
    Integer domId;
    Integer resTypeId;
    Integer opId;
    String opName;
    String opDes;
}
