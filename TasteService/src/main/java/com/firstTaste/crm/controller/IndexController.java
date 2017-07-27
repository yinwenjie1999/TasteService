package com.firstTaste.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author yinwenjie
 */
@Controller
@RequestMapping("/index")
public class IndexController extends BasicController {
	/**
	 * 创建用户信息
	 * @param customerInfo
	 * @return 
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView createCustomerInfo() {
		ModelAndView mv = new ModelAndView("index");
		return mv;
	}
}
