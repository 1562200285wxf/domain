package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomOwnerRes;
import cn.itsmith.sysutils.resacl.entities.DomOwnerUser;
import cn.itsmith.sysutils.resacl.entities.DomResType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DomResTypeMapper {
    DomResType selectById(@Param("domId") int domId, @Param("resTypeId") int resTypeId);
    List<DomResType> selectByOwnerId(int ownerId);

//liu
//插入==================================================================
//插入OwnerRes
public int addOwnerRes(DomOwnerRes domOwnerRes);
    //【一开始的varify只是验证域标识和token是否匹配】，所以接下来对||逻辑表||的任何操作都必须传入你一开始选择的domid
    //查询OwnerRes表中所有类型id(删除类型也会用到此接口，OwnerRes表种有类型才能删)
    public List<Integer> queryAllResIdByDomId(@Param("domId") Integer domId,@Param("ownerId") Integer ownerId);
    //查询Res表中能拿到的类型id,和user不同的是这里的query2仍是个逻辑表，所以还需要domid
    public List<Integer> queryAllResId2(Integer domId);
    public int updataStatus(DomOwnerRes domOwnerRes);

    //删除=================================================================
    //删除DomRes
    public int deleteType(@Param("domId") Integer domId,
                          @Param("ownerId") Integer ownerId,
                          @Param("resTypeId") Integer resTypeId);

    //查询================================================================
    public DomOwnerRes queryOwnerRes(@Param("domId")Integer domId ,
                                     @Param("ownerId") Integer ownerId,
                                     @Param("resTypeId") Integer resTypeId);//查询一条对象用于删除这条数据前将这条数据返回给前端
    //
    public List<DomOwnerRes> queryResBydomowner(@Param("domId") Integer domId, @Param("ownerId") Integer ownerId);
    //查询基本表
    public DomResType queryResBase(@Param("domId") Integer domId, @Param("resTypeId") Integer resTypeId);
    public List<DomResType> queryBaseByPid(@Param("domId") Integer domId,@Param("pId") Integer pId);


    //wang
    int deleteByPrimaryKey(Integer resTypeId);

    int insert(DomResType record);

    int insertSelective(DomResType record);

    DomResType selectByPrimaryKey(Integer resTypeId);

    int updateByPrimaryKeySelective(DomResType record);

    int updateByPrimaryKey(DomResType record);

    //自己写的
    List<DomResType> getDomResTypes(Integer domid);
}
