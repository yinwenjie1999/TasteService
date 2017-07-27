package com.firstTaste.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firstTaste.crm.entity.CommodityEntity;

/**
 * 和商品信息数据操作直接相关的定义
 * @author yinwenjie
 */
public interface CommodityRepository extends JpaRepository<CommodityEntity, String> { 
	
}
