package com.firstTaste.crm.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.firstTaste.crm.entity.CommodityEntity;
import com.firstTaste.crm.entity.CustomerInfoEntity;
import com.firstTaste.crm.entity.OrderInfoEntity;
import com.firstTaste.crm.entity.WorkInfoEntity;
import com.firstTaste.crm.repository.CommodityRepository;
import com.firstTaste.crm.repository.CustomerInfoRepository;
import com.firstTaste.crm.repository.OrderInfoRepository;
import com.firstTaste.crm.repository.WorkInfoRepository;
import com.firstTaste.crm.services.iface.OrderInfoService;

/**
 * @author yinwenjie
 */
@Service("OrderInfoServiceImpl")
public class OrderInfoServiceImpl implements OrderInfoService {

	@Autowired
	private OrderInfoRepository orderInfoRepository;
	
	@Autowired
	private CommodityRepository commodityRepository;
	
	@Autowired
	private WorkInfoRepository workInfoRepository;
	
	@Autowired
	private CustomerInfoRepository customerInfoRepository;

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#createOrderInfo(com.firstTaste.crm.entity.OrderInfoEntity)
	 */
	@Transactional
	@Override
	public OrderInfoEntity createOrderInfo(OrderInfoEntity orderInfo) {
		/*
		 * 创建订单过程如下：
		 * 0、查询商品信息，目前就只有一种商品，写死了的
		 * 
		 * 1、创建订单信息本身，其中以下信息必须有
		 * 	订单地址、开始时间、关联客户、送货次数、应付和实付金额、商品信息
		 *    收货人、收货人联系电话
		 * 
		 * 以下信息不一定有：
		 * 	合作商户渠道、推荐人信息
		 * 
		 * 2、根据订单的开始时间、送货次数等信息，计算生成工单信息
		 * */
		CustomerInfoEntity customerInfo = orderInfo.getOrderer();
		if(customerInfo == null) {
			throw new IllegalArgumentException("未发现用户编号信息!");
		}
		String customerId = customerInfo.getId();
		if(StringUtils.isEmpty(customerId)) {
			throw new IllegalArgumentException("未发现用户编号信息!");
		}
		customerInfo = customerInfoRepository.findOne(customerId);
		orderInfo.setOrderer(customerInfo);
		
		// 0、========(商品)
		CommodityEntity commodity = orderInfo.getCommodity();
		if(commodity == null || StringUtils.isEmpty(commodity.getId())) {
			throw new IllegalArgumentException("商品信息必须选择!");
		}
		CommodityEntity currentCommodity = commodityRepository.findOne(commodity.getId());
		
		// 1、========
		String address = orderInfo.getAddress();
		Date beginTime = orderInfo.getBeginTime();
		Date createTime = orderInfo.getCreateTime();
		Integer workNumber = orderInfo.getWorkNumber();
		Float ablePay = orderInfo.getAblePay();
		Float realPay = orderInfo.getRealPay();
		String receiver = orderInfo.getReceiver();
		String receiverPhone = orderInfo.getReceiverPhone();
		
		if(StringUtils.isEmpty(address)) {
			throw new IllegalArgumentException("必须填写地址信息!");
		}
		Date nowTime = new Date();
		Calendar calendar = Calendar.getInstance();
		List<WorkInfoEntity> workInfos = new ArrayList<>();
		// 注意calendar不是线程安全的
		synchronized (calendar) {
			calendar.setTime(nowTime);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),0,0,0);
			if(beginTime== null) {
				// 默认为下周一配送
				int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				calendar.add(Calendar.DATE , -dayOfWeek + 1 + 7 + 1);
				beginTime = calendar.getTime();
				orderInfo.setBeginTime(beginTime);
				System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(beginTime));
			} else {
				// 开始时间必须为今天以后
				calendar.add(Calendar.DATE, 1);
				Date minBeginTime = calendar.getTime();
				if(minBeginTime.getTime() > beginTime.getTime()) {
					throw new IllegalArgumentException("最早为明天配送!");
				}
			}
			if(createTime == null) {
				createTime = new Date();
				orderInfo.setCreateTime(createTime);
			}
			if(workNumber <= 0) {
				workNumber = 4;
				orderInfo.setWorkNumber(workNumber);
			}
			if(realPay < 0.0f) {
				throw new IllegalArgumentException("应收金额，必须填写!");
			}
			// 商品信息
			orderInfo.setCommodity(currentCommodity);
			if(ablePay <= 0.0f) {
				ablePay = currentCommodity.getSolePrice();
				orderInfo.setAblePay(ablePay);
			}
			// 收货人：收货人如果没有填写，则默认为订货人
			if(StringUtils.isEmpty(receiver)) {
				receiver = customerInfo.getName();
				orderInfo.setReceiver(receiver);
			}
			// 收货人联系方式：如果没有填写，则默认为订货人的电话
			if(StringUtils.isEmpty(receiverPhone)) {
				receiverPhone = customerInfo.getPhone();
				orderInfo.setReceiverPhone(receiverPhone);
			}
			// 配送结束时间
			calendar.setTime(beginTime);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),0,0,0);
			// 不止送一次，否则首次配送时间就是配送截至时间
			if(workNumber != 1) {
				calendar.add(Calendar.DATE, 7 * (workNumber - 1));
			}
			orderInfo.setEndTime(calendar.getTime());
			
			this.orderInfoRepository.saveAndFlush(orderInfo);
			
			// 2、============== 工单信息
			calendar.setTime(beginTime);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),0,0,0);
			for(int index = 1 ; index <= workNumber ; index++) {
				WorkInfoEntity workInfoEntity = new WorkInfoEntity();
				workInfoEntity.setAddress(address);
				// 送货时间
				workInfoEntity.setDeliveryTime(calendar.getTime());
				// 然后增加7提案
				calendar.add(Calendar.DATE, 7);
				workInfoEntity.setInfoType(1);
				workInfoEntity.setOrderInfo(orderInfo);
				workInfoEntity.setPhone(customerInfo.getPhone());
				workInfoEntity.setQq(customerInfo.getQq());
				workInfoEntity.setWeixin(customerInfo.getWeixin());
				workInfoEntity.setReceiver(receiver);
				workInfoEntity.setReceiverPhone(receiverPhone);
				workInfoEntity.setStatus(0);
				
				workInfoEntity.setOrderer(customerInfo.getName());
				this.workInfoRepository.save(workInfoEntity);
				workInfos.add(workInfoEntity);
			}
		}
		
		orderInfo.setWorkInfos(workInfos);
		return orderInfo;
	} 
	
	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#findByCustomerId(java.lang.String)
	 */
	@Override
	public List<OrderInfoEntity> findByCustomerId(String customerId) {
		if(StringUtils.isEmpty(customerId)) {
			throw new IllegalArgumentException("客户编号信息必须传入!");
		}
		List<OrderInfoEntity> results =  this.orderInfoRepository.findByCustomerId(customerId);
		
		// 由于是基本信息，所以部分信息不用返回
		for (OrderInfoEntity orderInfo : results) {
			orderInfo.setOrderer(null);
			orderInfo.setIntroducer(null);
			orderInfo.setSupplierChannel(null);
			orderInfo.setWorkInfos(null);
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#findByOrderId(java.lang.String)
	 */
	public OrderInfoEntity findByOrderId(String orderId) {
		if(StringUtils.isEmpty(orderId)) {
			throw new IllegalArgumentException("订单编号信息必须传入!");
		}
		
		OrderInfoEntity orderInfo =  this.orderInfoRepository.findByOrderId(orderId);
		return orderInfo;
	}

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#findByConditions(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<OrderInfoEntity> findByConditions(String orderids, String phone, String weixin , String receiver , String receiverPhone) {
		if(StringUtils.isEmpty(orderids) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(weixin)
				&& StringUtils.isEmpty(receiver) && StringUtils.isEmpty(receiverPhone)) {
			throw new IllegalArgumentException("至少输入一种联系方式，作为查询条件!");
		}
		
		List<OrderInfoEntity> results = this.orderInfoRepository.findAll(new Specification<OrderInfoEntity>() {
			@Override
			public Predicate toPredicate(Root<OrderInfoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				root.fetch("commodity", JoinType.LEFT);
				root.fetch("orderer", JoinType.LEFT);
				// 查询的orderids
				if (!StringUtils.isEmpty(orderids)) {
	                list.add(cb.like(root.get("id").as(String.class), orderids));
	            }
				// 查询的weixin
				if (!StringUtils.isEmpty(weixin)) {
	                list.add(cb.equal(root.get("orderer").get("weixin").as(String.class), weixin));
	            }
				// 查询的phone
				if (!StringUtils.isEmpty(phone)) {
	                list.add(cb.equal(root.get("orderer").get("phone").as(String.class), phone));
	            }
				// 查询的receiver
				if (!StringUtils.isEmpty(receiver)) {
	                list.add(cb.equal(root.get("receiver").as(String.class), receiver));
	            }
				// 查询的receiverPhone
				if (!StringUtils.isEmpty(receiverPhone)) {
	                list.add(cb.equal(root.get("receiverPhone").as(String.class), receiverPhone));
	            }
				
				Predicate[] p = new Predicate[list.size()];
	            return cb.and(list.toArray(p));
			}
		});
		
		return results;
	}

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#updateOrderInfo(com.firstTaste.crm.entity.OrderInfoEntity)
	 */
	@Transactional
	@Override
	public OrderInfoEntity updateOrderInfo(OrderInfoEntity orderInfo) {
		if(orderInfo == null) {
			throw new IllegalArgumentException("必须传入修改的订单信息!");
		}
		String orderId = orderInfo.getId();
		OrderInfoEntity currentOrderInfo = this.orderInfoRepository.findOne(orderId);
		if(currentOrderInfo == null) {
			throw new IllegalArgumentException("没有发现对应的订单信息！");
		}
		
		/*
		 * 1、首先修改订单基本信息，只能修改的信息包括：
		 * 收货人信息、备注信息、应付金额、实付金额
		 * 2、接着重新更新该订单截止当前没有配送的工单中的冗余信息
		 * */
		String address = orderInfo.getAddress();
		if(StringUtils.isEmpty(address)) {
			throw new IllegalArgumentException("送货地址必须填写!");
		}
		String receiver = orderInfo.getReceiver();
		if(StringUtils.isEmpty(receiver)) {
			throw new IllegalArgumentException("收货人必须填写!");
		}
		String receiverPhone = orderInfo.getReceiverPhone();
		if(StringUtils.isEmpty(receiverPhone)) {
			throw new IllegalArgumentException("收货人电话必须填写!");
		}
		
		// 1、=============
		currentOrderInfo.setAblePay(orderInfo.getAblePay());
		currentOrderInfo.setAddress(address);
		currentOrderInfo.setRealPay(orderInfo.getRealPay());
		currentOrderInfo.setReceiver(receiver);
		currentOrderInfo.setReceiverPhone(receiverPhone);
		currentOrderInfo.setRemark(orderInfo.getRemark());
		this.orderInfoRepository.saveAndFlush(currentOrderInfo);
		
		// 2、=============
		Calendar calendar = Calendar.getInstance();
		Date currentDeliveryTime = null;
		synchronized (calendar) {
			calendar.setTime(new Date());
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) , 0 , 0, 0);
			currentDeliveryTime = calendar.getTime();
		}
		List<WorkInfoEntity> results =  this.workInfoRepository.findNoDeliveryByOrderId(currentDeliveryTime, orderId);
		for (WorkInfoEntity workInfo : results) {
			workInfo.setAddress(address);
			workInfo.setReceiver(receiver);
			workInfo.setReceiverPhone(receiverPhone);
			this.workInfoRepository.save(workInfo);
		}
		
		return currentOrderInfo;
	}

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#updateOrderDelayTime(java.lang.String, java.util.Date)
	 */
	@Transactional
	@Override
	public OrderInfoEntity updateOrderDelayTime(String orderid, Date delayTime) {
		if(StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("必须指定订单编号!");
		}
		OrderInfoEntity currentOrderInfo = this.orderInfoRepository.findOne(orderid);
		if(currentOrderInfo == null) {
			throw new IllegalArgumentException("没有发现对应的订单信息！");
		}
		Date nowTime = new Date();
		if(delayTime == null) {
			throw new IllegalArgumentException("新的配送时间必须填写！");
		}
		if(delayTime.getTime() <= nowTime.getTime()) {
			throw new IllegalArgumentException("不能修改时间到一个已过去的时间！");
		}
		
		/*
		 * 操作过程为：
		 * 1、首先确定当前新的配送时间，是在今天以后（至少是一天以后）
		 * 以及其它有效性
		 * 2、查询当前订单下是否有截止当天还没有配送的工单信息，且这个订单也是有效的
		 * 3、对这些配送单进行批量延期，7天为一个周期
		 * */
		// 1、========
		if(StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("订单编号必须填写！");
		}
		
		// 判断时间，必须在一天以后
		Calendar  calendar = Calendar.getInstance();
		Date nowDate = null;
		Date newDeliveryDate = null;
		synchronized (calendar) {
			calendar.setTime(nowTime);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
			nowDate = calendar.getTime();
			
			calendar.setTime(delayTime);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
			newDeliveryDate = calendar.getTime();
			if(nowDate.getTime() >= newDeliveryDate.getTime()) {
				throw new IllegalArgumentException("新的配送时间必须要在一天以后！");
			}
			
			// 2、=========
			List<WorkInfoEntity> results = this.workInfoRepository.findNoDeliveryByOrderId(nowDate, orderid);
			for (WorkInfoEntity workInfoEntity : results) {
				// 3、=========
				workInfoEntity.setDeliveryTime(newDeliveryDate);
				calendar.add(Calendar.DATE, 7);
				newDeliveryDate = calendar.getTime();
				this.workInfoRepository.save(workInfoEntity);
			}
		}
		
		return currentOrderInfo;
	}

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.OrderInfoService#updateOrderWorkNumber(java.lang.String, java.lang.Integer)
	 */
	@Transactional
	@Override
	public OrderInfoEntity updateOrderWorkNumber(String orderid, Integer newWorkNumber) {
		if(StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("必须指定订单编号!");
		}
		OrderInfoEntity currentOrderInfo = this.orderInfoRepository.findOne(orderid);
		if(currentOrderInfo == null) {
			throw new IllegalArgumentException("没有发现对应的订单信息！");
		}
		if(newWorkNumber == null) {
			throw new IllegalArgumentException("新的配送数量必须填写！");
		}
		
		/*
		 * 1、工单数量的调整有两种情况
		 * 	1.1、增加工单，即newWorkNumber > workNumber
		 * 	如果是这种情况，那么就是在订单最后一张工单的基础上增加 newWorkNumber - workNumber个新的工单
		 * 	注意，新增的工单性质应该为“活动赠送”
		 * 	1.2、减少工单，即newWorkNumber < workNumber && newWorkNumber > workExecutedNumber，
		 * 	如果条件成立，说明需要从订单的最后一次开始，减少 workNumber - newWorkNumber次配送
		 * 
		 * 2、更新订单上的冗余数据
		 * */
		// 1.1 =======
		Integer workNumber = currentOrderInfo.getWorkNumber();
		Integer workExecutedNumber = currentOrderInfo.getWorkExecutedNumber();
		if(newWorkNumber > workNumber) {
			// 增加配送次数
			List<WorkInfoEntity> workInfos = this.workInfoRepository.findByOrderId(orderid);
			int size =  workInfos.size();
			// 最后一条
			WorkInfoEntity lastWorkInfo = workInfos.get(size - 1);
			Date lastDeliveryTime = lastWorkInfo.getDeliveryTime();
			// 开始接着计算新的时间
			Calendar calendar = Calendar .getInstance(); 
			Date newDeliveryTime = lastDeliveryTime;
			synchronized (calendar) {
				for(int index = 0 ; index < newWorkNumber - workNumber ; index++) {
					calendar.setTime(newDeliveryTime);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
					calendar.add(Calendar.DATE, 7);
					newDeliveryTime = calendar.getTime();
					
					// 构建工单基本信息
					WorkInfoEntity newLastWorkInfo = new WorkInfoEntity();
					newLastWorkInfo.setAddress(currentOrderInfo.getAddress());
					newLastWorkInfo.setDeliveryTime(newDeliveryTime);
					// 这里都填写活动赠送
					newLastWorkInfo.setInfoType(2);
					newLastWorkInfo.setOrderInfo(currentOrderInfo);
					newLastWorkInfo.setOrderer(lastWorkInfo.getOrderer());
					newLastWorkInfo.setPhone(lastWorkInfo.getPhone());
					newLastWorkInfo.setQq(lastWorkInfo.getQq());
					newLastWorkInfo.setWeixin(lastWorkInfo.getWeixin());
					newLastWorkInfo.setStatus(0);
					newLastWorkInfo.setReceiver(lastWorkInfo.getReceiver());
					newLastWorkInfo.setReceiverPhone(lastWorkInfo.getReceiverPhone());
					newLastWorkInfo.setRemark(lastWorkInfo.getRemark());
					
					this.workInfoRepository.save(newLastWorkInfo);
				}
			}
			
		} else if(newWorkNumber < workNumber && newWorkNumber > workExecutedNumber) {
			// 确定时间
			Calendar calendar = Calendar .getInstance(); 
			Date deliveryTime = new Date();
			synchronized (calendar) {
				calendar.setTime(deliveryTime);
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
				deliveryTime = calendar.getTime();
			}
			// 减少配送次数
			List<WorkInfoEntity> workInfos = this.workInfoRepository.findNoDeliveryByOrderId(deliveryTime, orderid);
			int size =  workInfos.size();
			for(int index = size - 1 , number = 0 ; number <  workNumber - newWorkNumber; number++,index--) {
				WorkInfoEntity currentWorkInfo = workInfos.get(index);
				this.workInfoRepository.delete(currentWorkInfo);
			}
		} else {
			throw new IllegalArgumentException("输入的新的配送次数不正确，请检查！");
		}
		
		// 2、===========
		currentOrderInfo.setWorkNumber(newWorkNumber);
		this.orderInfoRepository.save(currentOrderInfo);
		
		return currentOrderInfo;
	}
} 
