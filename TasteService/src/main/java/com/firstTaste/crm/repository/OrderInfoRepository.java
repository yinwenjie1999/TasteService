package com.firstTaste.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.firstTaste.crm.entity.OrderInfoEntity;

/**
 * 和订单数据相关的操作都在这里
 * @author yinwenjie
 */
public interface OrderInfoRepository extends JpaRepository<OrderInfoEntity, String> ,  JpaSpecificationExecutor<OrderInfoEntity> {
	
	/**
	 * 按照客户编号，查询这个用户下的订单信息
	 * @param customerId
	 * @return
	 */
	@Query(value=" from OrderInfoEntity o "
						+ " left join fetch o.commodity c "
						+ " left join fetch o.orderer cu "
						+ " where cu.id = :customerId order by o.beginTime ")
	public List<OrderInfoEntity> findByCustomerId(@Param("customerId") String customerId);
	
	/**
	 *  查询指定的订单基本信息，还包括订单的商品信息、工单信息。不包括其它关联信息
	 * @param customerId
	 * @return
	 */
	@Query(value=" from OrderInfoEntity o "
						+ " left join fetch o.commodity c "
						+ " left join fetch o.orderer cu "
						+ " where o.id = :orderId ")
	public OrderInfoEntity findByOrderId(@Param("orderId") String orderId);
	
	/**
	 * 将指定的订单中冗余的工单总数量+1
	 * @param customerId
	 */
	@Modifying
	@Query(value = "UPDATE crm_orderinfo SET work_number = work_number + 1 WHERE id = :orderinfoId", 
	      nativeQuery = true)
	public void incrementWorkNumber(@Param("orderinfoId") String orderinfoId);
	
	/**
	 * 将指定的订单中冗余的已完成工单总数量+1
	 * @param customerId
	 */
	@Modifying
	@Query(value = "UPDATE crm_orderinfo SET work_executed_number = work_executed_number + 1 WHERE id = :orderinfoId", 
	      nativeQuery = true)
	public void incrementWorkExecutedNumber(@Param("orderinfoId") String orderinfoId);
}
