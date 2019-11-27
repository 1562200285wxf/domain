package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DomOwnerResMapper {
    DomOwnerRes selectById(@Param("domId") int domId, @Param("ownerId") int ownerId, @Param("resTypeId") int resTypeId);
    List<DomOwnerRes> selectByOwnerId(int ownerId);

    //wang
    DomOwnerRes selectByPrimaryKey(Integer ID);
}
