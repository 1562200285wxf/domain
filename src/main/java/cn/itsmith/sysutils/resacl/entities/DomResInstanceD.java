package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;
/**
 * 用于DomResInstanceController删除资源实例的工具类
 */
@Data
public class DomResInstanceD {
    Integer domId;
    Integer ownerId;
    Integer resId;
    Integer resTypeId;
}
