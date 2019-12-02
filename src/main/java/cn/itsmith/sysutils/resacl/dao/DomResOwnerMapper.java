package cn.itsmith.sysutils.resacl.dao;

import cn.itsmith.sysutils.resacl.entities.DomResOwner;
import cn.itsmith.sysutils.resacl.entities.DomResOwnerR;
import cn.itsmith.sysutils.resacl.entities.DomResOwnerU;
import cn.itsmith.sysutils.resacl.entities.DomResOwnerUU;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DomResOwnerMapper {
   List<DomResOwner> getAllDomResOwner();
   DomResOwner selectById(@Param("domId") int domId, @Param("ownerId") int ownerId);

   int insert(DomResOwner domResOwner);
   int updateOwnerDes(@Param("ownerId") int ownerId, @Param("ownerDes") String ownerDes);
   int deleteById(int ownerId);
   int updateStatus(DomResOwner domResOwner);
   List<DomResOwner> selectByPId(@Param("domId") int domId, @Param("pId") int pId);


//liu
   //这个ownerids是从Owner表中查出来的，用于插入DomUser验证
   public List<Integer> queryAllOwnerId(Integer domId);

   //这个ownerids是从user逻辑表中查出来的,因为删除/设置取消管理员是仅针对user逻辑表的
   //删除/设置取消管理员是插入以后的操作，这个ownerids在owner基本表中一定是存在的，意味着在插入的时候已经判断过owner基本表了【这里不再考虑基本表的事情】
   //意味着只要user逻辑表中有ownerid就行了
   //（其实这里还是不需要，废弃X....要想知道是否已经存在只需在usermapper根据（domId,ownerId,userId）在user逻辑表中查找整条数据存在即可）
   public List<Integer> queryAllOwnerIdFormUser(Integer domId);//(其实这里还是不需要，废弃X)
   //这个ownerids是从ownerRes逻辑表中查出来的
   public List<Integer> queryAllOwnerIdFromOwnerRes(Integer domId);//(其实这里还是不需要，废弃X)




   //xxx
//递归属主
   DomResOwnerUU selectUById(@Param("domId") int domId, @Param("ownerId") int ownerId);
   List<DomResOwnerUU> selectUByPId(@Param("domId") int domId, @Param("pId") int pId);

   DomResOwnerR selectRById(@Param("domId") int domId, @Param("ownerId") int ownerId);
   List<DomResOwnerR> selectRByPId(@Param("domId") int domId, @Param("pId") int pId);
   //查询owner
   DomResOwner queryOwnerBydomowner(@Param("domId") int domId, @Param("ownerId") int ownerId);

}
