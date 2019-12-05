package cn.itsmith.sysutils.resacl.entities;

import lombok.Data;

@Data
public class Room {
    Integer resId;
    String name;
    String description;
    Integer deskNo;
    Integer volume;
}
