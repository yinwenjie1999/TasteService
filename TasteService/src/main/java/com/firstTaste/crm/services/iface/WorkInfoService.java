package com.firstTaste.crm.services.iface;

import java.util.Date;
import java.util.List;

import com.firstTaste.crm.entity.WorkInfoEntity;

/**
 * 和工单有关的服务
 * @author yinwenjie
 */
public interface WorkInfoService {
	/**
	 * 为当前有效的，还没有执行完成的订单添加一个"赠送"性质的工单<br>
	 * 配送时间为当前订单对应的最后一次工单 +7 天
	 * @param orderInfoId 指定的订单信息
	 * @return
	 */
	public WorkInfoEntity createByOrderInfo(String orderInfoId);
	
	/**
	 * 对指定的工单执行完成（注意要顺带更改订单的冗余信息）
	 * @param workInfoId 工单编号
	 * @return
	 */
	public WorkInfoEntity doneWorkInfo(String workInfoId);
	
	/**
	 * 修改指定工单的备注信息
	 * @param workInfoId 
	 * @param remark 
	 */
	public void updateRemarkByWorkId(String workInfoId , String remark);
	
	/**
	 * 修改指定工单的运单信息
	 * @param workInfoId 指定的工单编号
	 * @param logisticsNo  指定的运单信息
	 */
	public void updateLogisticsNoByWorkId(String workInfoId , String logisticsNo);
	
	/**
	 * 对于指定的工单，发送配送工单信息
	 * @param workInfoId 指定的工单信息
	 * @param mobile 发送的手机号，可以不传入，如果不传入则默认使用工单的收货电话
	 * @throws IllegalArgumentException
	 */
	public void sendMsg(String workInfoId , String mobile) throws IllegalArgumentException;
	
	/**
	 * 按照订单编号，查询这个订单下的工单信息
	 * @param orderInfoId
	 * @return
	 */
	public List<WorkInfoEntity> findByOrderId(String orderInfoId);
	
	/**
	 * 查询计划于deliveryTime这一天进行配送的工单信息
	 * @param deliveryTime
	 * @return
	 */
	public List<WorkInfoEntity> findByDeliveryTime(Date deliveryTime);
	
	/**
	 * 按照可选择的工单查询条件，查询满足条件的订单信息
	 * @return
	 */
	public List<WorkInfoEntity> findByConditions(String phone , String weixin , String receiver , String receiverPhone , Date beginDeliveryTime , Date endDeliveryTime);
	
	/**
	 * @param orderInfo 新的订单信息
	 * @return
	 */
	public WorkInfoEntity findById(String workInfoId);
}
