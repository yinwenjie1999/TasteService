package com.firstTaste.crm.controller.restful;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.firstTaste.crm.controller.BasicController;
import com.firstTaste.crm.entity.CustomerInfoEntity;
import com.firstTaste.crm.services.iface.CustomerInfoService;

/**
 * @author yinwenjie
 */
@RestController
@RequestMapping("/v1/customers")
public class CustomerInfoController extends BasicController {
	
//	/**
//	   * 日志.
//	   */
//	  private static final Logger LOG = LoggerFactory.getLogger(CustomerInfoController.class);
	
	@Autowired
	private CustomerInfoService customerInfoService;
	
	/**
	 * 创建用户信息
	 * @param customerInfo
	 * @return 
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public CustomerInfoEntity createCustomerInfo(@RequestBody CustomerInfoEntity customerInfo) {
		customerInfo.setCreateTime(new Date());
		this.customerInfoService.createCustomerInfo(customerInfo);
		return customerInfo;
	}
	
	/**
	 * 查询所有的客户信息（只包括基本资料）
	 * TODO 实际上这个方法然并卵
	 * @return 
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<CustomerInfoEntity> findCustomerInfos() {
		return this.customerInfoService.findCustomerInfos();
	}
	
	/**
	 * 查询指定客户的基本信息，只有基本信息
	 * @param customerId 客户编号
	 * @return 
	 */
	@RequestMapping(value = "/getOne/{customerId}", method = RequestMethod.GET)
	public CustomerInfoEntity findCustomerInfo(@PathVariable("customerId") String customerId) {
		CustomerInfoEntity  customerInfo = this.customerInfoService.findCustomerInfo(customerId);
		return customerInfo;
	}
	
	/**
	 * 按条件查询指定的客户信息
	 * @return
	 */
	@RequestMapping(value = "/conditions", method = RequestMethod.GET)
	public List<CustomerInfoEntity> findCustomerInfo(String phone , String weixin, String qq) {
		return this.customerInfoService.findByCondition(phone, weixin, qq);
	}
}
