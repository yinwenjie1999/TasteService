package com.firstTaste.crm.services.iface;

import java.util.List;

import com.firstTaste.crm.entity.CustomerInfoEntity;

/**
 * @author yinwenjie
 */
public interface CustomerInfoService {
	/**
	 * 添加一个用户的基本信息，注意只有基本信息的添加。不包括任何订单、优惠等关联信息
	 * @param customerInfo 
	 */
	public void createCustomerInfo(CustomerInfoEntity customerInfo);
	
	/**
	 * 查询一个指定的用户信息（不包括用户的订单、工单信息）
	 * @param customerId
	 * @return
	 */
	public CustomerInfoEntity findCustomerInfo(String customerId);
	
	/**
	 * 查询当前所有的用户信息，当然也只有基本信息。排序按照创建时间进行
	 * TODO 这个方法在用户量大了以后，不建议使用了
	 */
	public List<CustomerInfoEntity> findCustomerInfos();
	
	/**
	 * 按照用户联系方式，或者用户名信息，查询指定用户
	 * @param phone 可能输入的电话
	 * @param weixin 可能输入的微信号
	 * @param name 用户名
	 * @return 如果查询到了，就返回这个用户的基本信息；其它情况返回null
	 */
	public List<CustomerInfoEntity> findByCondition(String phone , String weixin , String qq);
}