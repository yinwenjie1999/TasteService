package com.firstTaste.crm.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstTaste.crm.entity.OrderInfoEntity;
import com.firstTaste.crm.entity.WorkInfoEntity;
import com.firstTaste.crm.services.iface.WorkInfoService;

@Controller
@RequestMapping("/workinfos")
public class WorkInfoHttpController extends BasicController {
	
	@Autowired
	private WorkInfoService workInfoService;
	
	/**
	 * 页面准备
	 * @return
	 */
	@RequestMapping(value="" , method=RequestMethod.GET)
	public String pageView() {
		return "/workinfos/index";
	}
	
	/**
	 * 只查询满足条件的订单基本信息
	 * @return
	 */
	@RequestMapping(value="/findByConditions" , method=RequestMethod.GET)
	public void findByConditions(String phone , String weixin , String receiver , String receiverPhone , String beginDeliveryTime , String endDeliveryTime, HttpServletResponse response) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 如果输入了时间，则要进行时间类型的转换
		Date currentBeginDeliveryTime = null;
		Date currentEndDeliveryTime = null;
		if(!StringUtils.isEmpty(beginDeliveryTime)) {
			currentBeginDeliveryTime = format.parse(beginDeliveryTime);
		}
		if(!StringUtils.isEmpty(endDeliveryTime)) {
			currentEndDeliveryTime = format.parse(endDeliveryTime);
		}
		
		List<WorkInfoEntity> results = this.workInfoService.findByConditions(phone , weixin , receiver , receiverPhone , currentBeginDeliveryTime , currentEndDeliveryTime);
		for (WorkInfoEntity workInfoEntity : results) {
			OrderInfoEntity orderInfo = workInfoEntity.getOrderInfo();
			orderInfo.setSupplierChannel(null);
			orderInfo.setWorkInfos(null);
			orderInfo.setIntroducer(null);
		}
		
		this.writeResponseMsg(response, results);
	}
}