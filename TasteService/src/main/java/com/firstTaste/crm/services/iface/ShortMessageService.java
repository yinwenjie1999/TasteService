package com.firstTaste.crm.services.iface;

/**
 * 短信发送服务
 * @author yinwenjie
 */
public interface ShortMessageService {
	/**
	 * 发送短信息
	 * @param logisticsNo 中通运单号
	 * @param mobile 请求的电话号码
	 * @return 如果发送成功，则返回0，其它情况返回非0，或者抛出异常
	 */
	public Integer sendMessage(String logisticsNo , String mobile) throws IllegalArgumentException; 
}
