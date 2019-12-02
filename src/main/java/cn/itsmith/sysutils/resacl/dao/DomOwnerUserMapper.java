package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.entities.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
@Mapper
public interface DomOwnerUserMapper {
    DomOwnerUser selectById(int domId, int ownerId, int userId);
    List<DomOwnerUser> selectByDomId(int domId);

    //liu
    //插入==================================================================
    //插入DomUser
    public int addDomUser(DomOwnerUser domOwnerUser);
    public int updataStatus(DomOwnerUser domOwnerUser);
    //【一开始的varify只是验证域标识和token是否匹配】，所以接下来对||逻辑表||的任何操作都必须传入你一开始选择的domid
    //查询dom_user中所有成员id（删除成员也会用到此接口）
    public List<Integer> queryAlluserIdsByDomId(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId);
    //查询user基本表中能拿到的成员id
    public List<Integer> queryAlluserId2();
    //删除=================================================================
    //删除DomUser
    public int deleteUser(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId,@Param("userId") Integer userId);
    //管理员
    //设置管理员
    public int setAdmin(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId,@Param("userId") Integer userId);
    //取消管理员
    public int cancelAdmin(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId,@Param("userId") Integer userId);


    //查询================================================================
    //查询一条对象用于删除这条数据前将这条数据返回给前端
    public DomOwnerUser queryUser(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId,@Param("userId") Integer userId);
    //根据domId, ownerId查询user
    public List<DomOwnerUser> queryUserBydomowner(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId);


   //查询名字
    public String queryUserName(Integer userId);
    //查询所有基本表select *
    public List<User> getAllBase();
    //级联删除user
    public int deleteUserCascading(String id);


}
