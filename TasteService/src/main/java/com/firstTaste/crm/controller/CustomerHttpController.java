package com.firstTaste.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/customers")
public class CustomerHttpController {
	/**
	 * 页面准备
	 * @return
	 */
	@RequestMapping(value="" , method=RequestMethod.GET)
	public String pageView() {
		return "/customers/index";
	}
}
