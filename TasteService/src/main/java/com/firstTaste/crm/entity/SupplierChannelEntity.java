package com.firstTaste.crm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 供应商渠道
 * @author yinwenjie
 */
@Getter
@Setter
@Entity
@Table(name="crm_supplierchannel")
public class SupplierChannelEntity extends UuidEntity {
	/**
	 * 供应商名称
	 */
	@Column(name="name" , length=64, nullable=false)
	private String name;
	/**
	 * 供应商地址
	 */
	@Column(name="address" , length=128, nullable=false)
	private String address;
}
