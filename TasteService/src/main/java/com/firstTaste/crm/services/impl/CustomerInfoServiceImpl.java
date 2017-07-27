package com.firstTaste.crm.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.firstTaste.crm.entity.CustomerInfoEntity;
import com.firstTaste.crm.repository.CustomerInfoRepository;
import com.firstTaste.crm.services.iface.CustomerInfoService;

/**
 * @author yinwenjie
 *
 */
@Service("CustomerInfoServiceImpl")
public class CustomerInfoServiceImpl implements CustomerInfoService {
	
	@Autowired
	private CustomerInfoRepository customerInfoRepository;
	
	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.CustomerInfoService#createCustomerInfo(com.firstTaste.crm.entity.CustomerInfoEntity)
	 */
	@Transactional
	@Override
	public void createCustomerInfo(CustomerInfoEntity customerInfo) {
		/*
		 * 创建一个客户信息，只包括这个客户的基本信息：
		 * 1、如果查询到当前微信、电话或者qq已经存在于系统中了，就不允许添加这个用户了
		 * 
		 * */
		String phone = customerInfo.getPhone();
		String qq = customerInfo.getQq();
		String weixin = customerInfo.getWeixin();
		
		if(StringUtils.isEmpty(phone)) {
			throw new IllegalArgumentException("客户电话号码必须填写!");
		}
		if(!StringUtils.isEmpty(phone) && this.customerInfoRepository.findByPhone(phone) != null) {
			throw new IllegalArgumentException("指定的电话号码已经存在!");
		}
		if(!StringUtils.isEmpty(qq) && this.customerInfoRepository.findByQq(qq) != null) {
			throw new IllegalArgumentException("指定的QQ号码已经存在!");
		}
		if(!StringUtils.isEmpty(weixin) && this.customerInfoRepository.findByWeixin(weixin) != null) {
			throw new IllegalArgumentException("指定的微信号码已经存在!");
		}
		
		this.customerInfoRepository.saveAndFlush(customerInfo);
	}
	
	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.CustomerInfoService#findCustomerInfos()
	 */
	@Override
	public List<CustomerInfoEntity> findCustomerInfos() {
		return this.customerInfoRepository.findAllOrderByTime();
	}

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.CustomerInfoService#findCustomerInfo(java.lang.String)
	 */
	@Override
	public CustomerInfoEntity findCustomerInfo(String customerId) {
		CustomerInfoEntity customer =  this.customerInfoRepository.findOne(customerId);
		return customer;
	}

	/* (non-Javadoc)
	 * @see com.firstTaste.crm.services.iface.CustomerInfoService#findByCondition(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<CustomerInfoEntity> findByCondition(String phone, String weixin, String qq) {
		if(StringUtils.isEmpty(phone) && StringUtils.isEmpty(weixin) && StringUtils.isEmpty(qq)) {
			throw new IllegalArgumentException("至少输入一种联系方式，作为查询条件!");
		}
		
		List<CustomerInfoEntity> results = this.customerInfoRepository.findAll(new Specification<CustomerInfoEntity>() {
			@Override
			public Predicate toPredicate(Root<CustomerInfoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				// 查询的phone
				if (!StringUtils.isEmpty(phone)) {
	                list.add(cb.equal(root.get("phone").as(String.class), phone));
	            }
				// 查询的weixin
				if (!StringUtils.isEmpty(weixin)) {
	                list.add(cb.equal(root.get("weixin").as(String.class), weixin));
	            }
				// 查询的qq
				if (!StringUtils.isEmpty(qq)) {
	                list.add(cb.equal(root.get("qq").as(String.class), qq));
	            }
				
				Predicate[] p = new Predicate[list.size()];
	            return cb.and(list.toArray(p));
			}
		});
		
		return results;
	}
}