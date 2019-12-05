package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

import java.util.List;

@Data
public class DomResInstanceR {
    List<TableData> tableData;
    List<Object> domResInstances;
}
