package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.Desk;
import cn.itsmith.sysutils.resacl.entities.Room;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InstanceMapper {
    List<Desk> selectAllDesk();
    List<Room> selectAllRoom();
}
