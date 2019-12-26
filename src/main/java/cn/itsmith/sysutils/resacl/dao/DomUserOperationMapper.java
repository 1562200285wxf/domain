package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomUserOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface DomUserOperationMapper {
    List<DomUserOperation> selectByResId1(int domId, int ownerId, int resTypeId, int resId);
    int deleteByResId(int domId, int ownerId, int resTypeId, int resId);
    int updateStatus(DomUserOperation domUserOperation);
    List<DomUserOperation> selectByOpId(int domId, int resTypeId, int opId);

    /**
     * 根据域、属主、资源种类、资源实例查询拥有的成员或属主
     * @param domUserOperation
     * @return
     */
    List<DomUserOperation> selectUsersOrOwners(DomUserOperation domUserOperation);

    //查询单个用户是否用于权限
    DomUserOperation  checkOpernationByDomUserOperation(DomUserOperation domUserOperation);

    List<DomUserOperation> selectByResId(@Param("domId") Integer domId,@Param("resId") Integer resId);
    DomUserOperation selectByWhole(DomUserOperation domUserOperation);
    List<DomUserOperation> selectOps(DomUserOperation domUserOperation);
    int insert(DomUserOperation domUserOperation);
    int delete(DomUserOperation domUserOperation);
    //删除用户同时删除资源授权
    List<DomUserOperation> selectByuId(@Param("domId") Integer domId,@Param("ownerId") Integer ownerId,@Param("userId") Integer userId);
    int deleteByuId(@Param("domId") Integer domId,@Param("ownerId") Integer ownerId,@Param("userId") Integer userId);
}
