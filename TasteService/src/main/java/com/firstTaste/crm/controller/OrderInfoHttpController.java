package com.firstTaste.crm.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstTaste.crm.entity.OrderInfoEntity;
import com.firstTaste.crm.services.iface.OrderInfoService;

@Controller
@RequestMapping("/orderinfos")
public class OrderInfoHttpController extends BasicController {
	
	@Autowired
	private OrderInfoService orderInfoService;
	
	/**
	 * 页面准备
	 * @return
	 */
	@RequestMapping(value="" , method=RequestMethod.GET)
	public String pageView() {
		return "/orderinfos/index";
	}
	
	/**
	 * 只查询满足条件的订单基本信息
	 * @param orderids
	 * @param phone
	 * @param weixin
	 * @param receiver
	 * @param receiverPhone
	 * @return
	 */
	@RequestMapping(value="/findByConditions" , method=RequestMethod.GET)
	public void findByConditions(String orderids , String phone , String weixin , String receiver , String receiverPhone , HttpServletResponse response) {
		List<OrderInfoEntity> results = this.orderInfoService.findByConditions(orderids, phone, weixin, receiver, receiverPhone);
		for (OrderInfoEntity orderInfoEntity : results) {
			orderInfoEntity.setSupplierChannel(null);
			orderInfoEntity.setWorkInfos(null);
			orderInfoEntity.setIntroducer(null);
		}
		
		this.writeResponseMsg(response, results);
	}
}
