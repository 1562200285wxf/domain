package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomResOperation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DomResOperationMapper {
    int insert(DomResOperation domResOperation);
    int delete(DomResOperation domResOperation);
    int updateStatus(DomResOperation domResOperation);
    int updateOpDes(DomResOperation domResOperation);
    int updateIsExtend(DomResOperation domResOperation);
    int updateIsCommon(DomResOperation domResOperation);
    List<DomResOperation> selectById(int domId, int resTypeId);
    DomResOperation select(DomResOperation domResOperation);
}
