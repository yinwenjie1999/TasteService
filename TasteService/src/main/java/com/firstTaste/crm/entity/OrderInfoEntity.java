package com.firstTaste.crm.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 订单信息
 * @author yinwenjie
 */
@Getter
@Setter
@Entity
@Table(name="crm_orderinfo")
public class OrderInfoEntity extends UuidEntity {
	/**
	 * 订单关联的用户信息：订货人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="customerInfo" , nullable=false)
	private CustomerInfoEntity orderer;
	/**
	 * 创建时间（下单时间）
	 */
	@Column(name="createTime" , nullable=false)
	private Date createTime;
	/**
	 * 订单首次执行时间（既是第一次应配送时间）
	 */
	@Column(name="beginTime" , nullable=false)
	private Date beginTime;
	
	/**
	 * 订单最后一次执行时间（既是最后一次应配送时间）
	 */
	@Column(name="endTime" , nullable=false)
	private Date endTime;
	/**
	 * 配送地址
	 */
	@Column(name="address" , length=128 , nullable=false)
	private String address;
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
	 * 对应的工单次数
	 */
	@Column(name="workNumber" , nullable=false)
	private Integer workNumber = 4;
	/**
	 * 工单已执行的次数
	 */
	@Column(name="workExecutedNumber" , nullable=false)
	private Integer workExecutedNumber = 0;
	/**
	 * 订单备注信息
	 */
	@Column(name="remark" , length=512 , nullable=false)
	private String remark = "";
	/**
	 * 应付金额
	 */
	@Column(name="ablePay" , nullable=false)
	private float ablePay = 0.0f;
	/**
	 * 实付金额
	 */
	@Column(name="realPay" , nullable=false)
	private float realPay = 0.0f;
	/**
	 * 商品信息（默认为99元包月套餐）
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="commodity" , nullable=false)
	private CommodityEntity commodity;
	/**
	 * 订单渠道
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplierChannel" , nullable=true)
	private SupplierChannelEntity supplierChannel;
	/**
	 * 订单介绍人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="introducer" , nullable=true)
	private CustomerInfoEntity introducer;
	/**
	 * 关联的工单信息
	 */
	@OneToMany(fetch=FetchType.LAZY )
	@JoinColumn(name="orderInfo")
	private List<WorkInfoEntity> workInfos;
}
