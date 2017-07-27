package com.firstTaste.crm.services.iface;

import java.util.Date;
import java.util.List;

import com.firstTaste.crm.entity.OrderInfoEntity;

/**
 * 和订单有关的服务
 * @author yinwenjie
 */
public interface OrderInfoService {
	/**
	 * 按照用户编号，查询这个用户下对应的订单信息，并按照订单创建时间排序
	 * @param customerId
	 * @return
	 */
	public List<OrderInfoEntity> findByCustomerId(String customerId);
	
	/**
	 * 查询指定的订单基本信息，还包括订单的商品信息、工单信息。不包括其它关联信息
	 * @param orderId  订单编号
	 * @return 
	 */
	public OrderInfoEntity findByOrderId(String orderId);
	
	/**
	 * 按照可选择的订单编号、订货人电话、微信号等，查询满足条件的订单信息
	 * @return
	 */
	public List<OrderInfoEntity> findByConditions(String orderids , String phone , String weixin , String receiver , String receiverPhone);
	
	/**
	 * 创建订单信息，包括订单信息关联的工单信息
	 * @param orderInfo 新的订单信息
	 * @return
	 */
	public OrderInfoEntity createOrderInfo(OrderInfoEntity orderInfo);
	
	/**
	 * 对已有订单和关联的工单进行修改操作<br>
	 * 注意，只有订单基本信息的修改，包括：收货人信息、备注信息、应付金额、实付金额<br>
	 * 如果要进行配送次数、配送时间上的修改，则需要通过updateOrderDelayTime方法或者updateOrderWorkNumber方法进行
	 * @param orderInfo
	 * @return
	 */
	public OrderInfoEntity updateOrderInfo(OrderInfoEntity orderInfo);
	
	/**
	 * 将订单未配送的工单进行延期，截止当前时间没有配送的工单全部需要延期，同样是7天为一个周期
	 * @param orderid
	 * @param delayTime
	 * @return
	 */
	public OrderInfoEntity updateOrderDelayTime(String orderid , Date delayTime);
	
	/**
	 * 将指定的订单增加配送工单（必须是没有结束配送的订单），注意可以增加配送数量，也可以减少配送数量<br>
	 * 但是截至当前已经配送完成的工单不能改变
	 * @param orderid
	 * @param workNumber
	 * @return
	 */
	public OrderInfoEntity updateOrderWorkNumber(String orderid , Integer workNumber);
}
