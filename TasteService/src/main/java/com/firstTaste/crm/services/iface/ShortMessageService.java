package com.firstTaste.crm.services.iface;

/**
 * 短信发送服务
 * @author yinwenjie
 */
public interface ShortMessageService {
	/**
	 * 发送短信息（非最后一次配送时发送）
	 * @param logisticsNo 中通运单号
	 * @param mobile 请求的电话号码
	 * @param noCount 订单的当前配送次数
	 * @param totalCount 订单的总配送次数
	 * @return 如果发送成功，则返回0，其它情况返回非0，或者抛出异常
	 */
	public Integer sendMessage(String logisticsNo , String mobile , Integer noCount , Integer totalCount) throws IllegalArgumentException; 
	
	/**
     * 发送短信息（最后一次配送时发送）
     * @param logisticsNo 中通运单号
     * @param mobile 请求的电话号码
     * @return 如果发送成功，则返回0，其它情况返回非0，或者抛出异常
     */
    public Integer sendMessageAtLast(String logisticsNo , String mobile) throws IllegalArgumentException; 
}
