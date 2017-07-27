package com.firstTaste.crm.controller.restful;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.firstTaste.crm.controller.BasicController;
import com.firstTaste.crm.entity.OrderInfoEntity;
import com.firstTaste.crm.services.iface.OrderInfoService;

/**
 *  TODO 明天开始测试了
 * @author yinwenjie
 */
@RestController
@RequestMapping("/v1/orderinfos")
public class OrderInfoController  extends BasicController {
	@Autowired
	private OrderInfoService orderInfoService;
	
	/**
	 * 创建订单信息
	 * @param customerInfo
	 * @return 
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public OrderInfoEntity createCustomerInfo(@RequestBody OrderInfoEntity orderInfo) {
		orderInfo.setCreateTime(new Date());
		this.orderInfoService.createOrderInfo(orderInfo);
		
		// 只返回订单基本信息就可以了
		orderInfo.setWorkInfos(null);
		return orderInfo;
	}
	
	/**
	 * 该rest接口只能进行订单基本信息的修改
	 * @param orderInfo
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.PATCH)
	public OrderInfoEntity upsertCustomerInfo(@RequestBody OrderInfoEntity orderInfo) {
		this.orderInfoService.updateOrderInfo(orderInfo);
		
		orderInfo.setWorkInfos(null);
		return orderInfo;
	}
	
	/**
	 * 对指定订单中未完成的工单进行配送时间的批量修改
	 * @param orderId
	 * @param newDeliveryTime
	 */
	@RequestMapping(value = "/delayWorkTime/{orderId}/{newDeliveryTime}", method = RequestMethod.PATCH)
	public OrderInfoEntity  delayWorkTimeByOrderId(@PathVariable("orderId") String orderId , @PathVariable("newDeliveryTime") String newDeliveryTime) throws ParseException {
		if(StringUtils.isEmpty(newDeliveryTime)) {
			throw new IllegalArgumentException("新的配送时间，必须传入!");
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		OrderInfoEntity result =  this.orderInfoService.updateOrderDelayTime(orderId, format.parse(newDeliveryTime));
		result.setCommodity(null);
		result.setIntroducer(null);
		result.setOrderer(null);
		result.setSupplierChannel(null);
		result.setWorkInfos(null);
		return result;
	}
	
	/**
	 * 对指定订单中未完成的工单数量进行批量修改
	 * @param orderId 
	 * @param workNumber 新的配送数量
	 */
	@RequestMapping(value = "/updateOrderWorkNumber/{orderId}/{workNumber}", method = RequestMethod.PATCH)
	public OrderInfoEntity updateOrderWorkNumber(@PathVariable("orderId") String orderid , @PathVariable("workNumber") Integer workNumber) {
		OrderInfoEntity result =  this.orderInfoService.updateOrderWorkNumber(orderid, workNumber);
		// 只返回订单基本信息就可以了
		result.setWorkInfos(null);
		result.setIntroducer(null);
		result.setSupplierChannel(null);
		result.setCommodity(null);
		result.setOrderer(null);
		return result;
	}
	
	/**
	 * 查询指定的订单基本信息，还包括订单的商品信息、工单信息。不包括其它关联信息
	 * @param orderId 
	 * @return 
	 */
	@RequestMapping(value = "/One/{orderId}", method = RequestMethod.GET)
	public OrderInfoEntity findByOrderId(@PathVariable("orderId") String orderId) {
		OrderInfoEntity result =  this.orderInfoService.findByOrderId(orderId);
		
		// 只返回订单基本信息就可以了
		result.setWorkInfos(null);
		result.setIntroducer(null);
		result.setSupplierChannel(null);
		return result;
	}
	
	@RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
	public List<OrderInfoEntity> findByCustomerId(@PathVariable("customerId") String customerId) {
		return this.orderInfoService.findByCustomerId(customerId);
	}
}