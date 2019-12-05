package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

/**
 * 用于动态改变前端表格的列
 */
@Data
public class TableData {
    String label;
    String prop;

    public TableData(String label, String prop) {
        this.label =label;
        this.prop = prop;
    }
}
