package com.firstTaste.crm.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.firstTaste.crm.entity.WorkInfoEntity;

/**
 * 和工单数据相关的操作都在这里
 * @author yinwenjie
 */
public interface WorkInfoRepository extends JpaRepository<WorkInfoEntity, String> , JpaSpecificationExecutor<WorkInfoEntity>{		
	/**
	 * 按照订单编号，查询这个订单下的工单信息
	 * @param orderInfoId
	 * @return
	 */
	@Query("from WorkInfoEntity w left join fetch w.orderInfo o where o.id = :orderInfoId order by w.deliveryTime ")
	public List<WorkInfoEntity> findByOrderId(@Param("orderInfoId") String orderInfoId);
	
	/**
	 * 查询计划于deliveryTime这一天进行配送的工单信息
	 * @param deliveryTime
	 */
	public List<WorkInfoEntity> findByDeliveryTime(@Param("deliveryTime") Date deliveryTime);
	
	/**
	 * 查询某个订单中截止当前时间(deliveryTime)没有被配送的工单信息
	 * @param deliveryTime 当前设定的时间
	 * @param orderInfoId
	 * @return
	 */
	@Query("from WorkInfoEntity w  where w.status = 0 and w.orderInfo.id = :orderInfoId and  w.deliveryTime > :deliveryTime order by w.deliveryTime ")
	public List<WorkInfoEntity> findNoDeliveryByOrderId(@Param("deliveryTime") Date deliveryTime , @Param("orderInfoId") String orderInfoId);
	
	/**
	 * 按照工单id查询这个工单，和这个工单对应的订单信息
	 * @param workinfoId
	 * @return
	 */
	@Query("from WorkInfoEntity w left join fetch w.orderInfo o  where w.id = :workinfoId")
	public  WorkInfoEntity findByWorkinfoId(@Param("workinfoId") String workinfoId);
	
	@Modifying
	@Query(value = "UPDATE crm_workinfo SET status = 1 WHERE id = :workInfoId", nativeQuery = true)
	public void doneWorkInfo(@Param("workInfoId") String workInfoId);
	
	/**
	 * 修改指定工单的备注信息
	 * @param workInfoId 
	 * @param remark 
	 */
	@Modifying
	@Query(value = "UPDATE crm_workinfo SET remark = :remark WHERE id = :workInfoId", nativeQuery = true)
	public void updateRemarkByWorkId(@Param("workInfoId") String workInfoId , @Param("remark") String remark);
	
	/**
     * 修改指定工单的运单信息
     * @param workInfoId 
     * @param logisticsNo 
     */
	@Modifying
    @Query(value = "UPDATE crm_workinfo SET logistics_no = :logisticsNo WHERE id = :workInfoId", nativeQuery = true)
	public void updateLogisticsNoByWorkId(@Param("workInfoId") String workInfoId, @Param("logisticsNo") String logisticsNo);
}
