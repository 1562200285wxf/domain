package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

/**
 * 用于动态改变前端表格的列
 */
@Data
public class InsAttributes {
    Integer id;
    Integer resTypeId;
    String label;
    String prop;
}
