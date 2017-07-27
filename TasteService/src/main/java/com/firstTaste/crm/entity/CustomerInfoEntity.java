package com.firstTaste.crm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户信息<br>
 * TODO 索引未建
 * @author yinwenjie
 */
@Getter
@Setter
@Entity
@Table(name="crm_customerinfo")
public class CustomerInfoEntity extends UuidEntity {
	/**
	 * 用户真实姓名
	 */
	@Column(name="name" , length=64 , nullable=false)
	private String name;
	/**
	 * 实际微信号
	 */
	@Column(name="weixin" , length=64, nullable=false)
	private String weixin = "";
	/**
	 * 微信订阅号
	 */
	@Column(name="weixinSubscribe" , length=64, nullable=false)
	private String weixinSubscribe = "";
	/**
	 * 电话号码
	 */
	@Column(name="phone" , length=64, nullable=false , unique=true)
	private String phone;
	/**
	 * qq
	 */
	@Column(name="qq" , length=64, nullable=false)
	private String qq = "";
	/**
	 * 创建时间 yyyy-MM-dd
	 */
	@Column(name="createTime" , nullable=false)
	private Date createTime = new Date();
}