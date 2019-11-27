package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.Domain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DomainMapper {
    Domain select(@Param("domId") int domId, @Param("domToken") String domToken);
    List<Domain> getDomainByToken(String domToken);


    //权限炎症，token和域id对应
    public Integer varify(@Param("domId")Integer domId, @Param("domToken")String domToken);

    //wang
    int deleteByPrimaryKey(Integer domId);

    int insert(Domain record);

    int insertSelective(Domain record);

    Domain selectByPrimaryKey(Integer domId);

    int updateByPrimaryKeySelective(Domain record);

    int updateByPrimaryKey(Domain record);
}
