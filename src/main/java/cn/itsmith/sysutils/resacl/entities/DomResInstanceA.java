package cn.itsmith.sysutils.resacl.entities;
import lombok.Data;

@Data
/**
 * 用于DomResInstanceController添加资源实例的工具类
 */
public class DomResInstanceA {
    Integer domId;
    Integer ownerId;
    Integer resTypeId;
    Integer resId;
    Integer resPid;
}
