package com.firstTaste.crm.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 工单信息
 * @author yinwenjie
 */
@Getter
@Setter
@Entity
@Table(name="crm_workinfo")
public class WorkInfoEntity extends UuidEntity {
	/**
	 * 工单对应的订单信息
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="orderInfo" , nullable=false)
	private OrderInfoEntity orderInfo;
	/**
	 * 工单需配送时间
	 */
	@Column(name="deliveryTime" , nullable=false)
	private Date deliveryTime;
	/**
	 * 工单产生原因<br>
	 * 1、订单自动产生
	 * 2、活动赠送
	 */
	@Column(name="infoType" ,  nullable=false)
	private Integer infoType;
	
	//=================订货人相关
	/**
	 * 订货人姓名
	 */
	@Column(name="orderer" , length=64, nullable=false)
	private String orderer;
	/**
	 *  订货人实际微信号
	 */
	@Column(name="weixin" , length=64, nullable=false)
	private String weixin = "";
	/**
	 *  订货人电话号码
	 */
	@Column(name="phone" , length=64, nullable=false)
	private String phone;
	/**
	 *  订货人qq
	 */
	@Column(name="qq" , length=64, nullable=false)
	private String qq = "";
	
	//=================收货人相关
	/**
	 * 收货人名称
	 */
	@Column(name="receiver" , length=64 , nullable=false)
	private String receiver;
	/**
	 * 收货人电话
	 */
	@Column(name="receiverPhone" , length=64 , nullable=false)
	private String receiverPhone;
	/**
	 * 配送地址
	 */
	@Column(name="address" , length=128 , nullable=false)
	private String address;
	
	//=================配送人相关
	/**
	 * 配送人
	 * TODO 本版边界
	 */
	@Column(name="deliveryAgent" , length=64 , nullable=false)
	private String deliveryAgent = "";
	/**
	 * 工单备注信息
	 */
	@Column(name="remark" , length=512 , nullable=false)
	private String remark = "";
	/**
	 * 工单状态<br>
	 * 0：未完成<br>
	 * 1：已完成（已送出）
	 */
	@Column(name="status" ,  nullable=false)
	private Integer status;
	
	/**
	 * 运单号
	 */
	@Column(name="logisticsNo" , length=256 , nullable=false)
	private String logisticsNo = "";
	
	/**
	 * 短信状态：是否已发送配送短信状态<br>
	 * 0：没有发送配送短信
	 * 1：已发送配送短信
	 */
	@Column(name="msgStatus" ,  nullable=false)
	private Integer msgStatus = 0;
}