package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomResInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface DomResInstanceMapper {
    int insert(DomResInstance domResInstance);
    int delete(DomResInstance domResInstance);
    int updateStatus(DomResInstance domResInstance);
    DomResInstance select(DomResInstance domResInstance);
    List<DomResInstance> selectByOwnerId(@Param("domId") int domId,
                                         @Param("ownerId") int ownerId);
//liu
    //判断该域下，类型是否正在被属主使用【属主拥有资源】
    public List<DomResInstance> beingUsed(@Param("domId") Integer domId,
                         @Param("ownerId") Integer ownerId,
                         @Param("resTypeId") Integer resTypeId);
}
