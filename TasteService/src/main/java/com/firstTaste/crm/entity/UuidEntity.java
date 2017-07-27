package com.firstTaste.crm.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;

/**
 * 各种实体层对象的定义中，只要包含持久层内部唯一编号的实例，都要集成该类.
 * 
 * @author yinwenjie
 */
@Getter
@Setter
@MappedSuperclass
public abstract class UuidEntity  {
  /**
   * 抽象实体层模型（MySQL主键）的编号信息.
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;
}
