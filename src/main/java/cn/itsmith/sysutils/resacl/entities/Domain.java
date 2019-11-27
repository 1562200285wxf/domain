package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

//域，
@Data
public class Domain {
    Integer domId;
    String domDes;
    String domToken;
    Integer status;
}
