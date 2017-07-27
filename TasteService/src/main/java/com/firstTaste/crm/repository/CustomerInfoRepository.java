package com.firstTaste.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.firstTaste.crm.entity.CustomerInfoEntity;

/**
 * 直接和数据层
 * @author yinwenjie
 */
public interface CustomerInfoRepository extends JpaRepository<CustomerInfoEntity, String> , JpaSpecificationExecutor<CustomerInfoEntity> {
	
	@Query("from CustomerInfoEntity c where c.name like :name or c.weixin = :weixin or c.phone = :phone order by c.createTime DESC")
	public CustomerInfoEntity  findByCondition(@Param("phone") String phone, 
			@Param("weixin") String weixin, @Param("name") String name);
	
	/**
	 * 查询所有客户，并按照创建时间排序
	 * @return
	 */
	@Query("from CustomerInfoEntity c  order by c.createTime DESC")
	public List<CustomerInfoEntity> findAllOrderByTime(); 
	
	public CustomerInfoEntity findByWeixin(String weixin);
	
	public CustomerInfoEntity findByPhone(String phone);
	
	public CustomerInfoEntity findByQq(String qq);
}