package com.firstTaste.crm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品信息
 * @author yinwenjie
 */
@Getter
@Setter
@Entity
@Table(name="crm_commodity")
public class CommodityEntity extends UuidEntity{
	/**
	 * 商品信息
	 */
	@Column(name="name" , length=64, nullable=false)
	private String name;
	/**
	 * 商品标准售价（没有任何优惠折扣、会员折扣的售价）
	 */
	@Column(name="solePrice", nullable=false)
	private Float solePrice = 0.0f;
	/**
	 * 商品是否有效，默认为有效
	 */
	@Column(name="effective", nullable=false)
	private boolean effective = true;
}