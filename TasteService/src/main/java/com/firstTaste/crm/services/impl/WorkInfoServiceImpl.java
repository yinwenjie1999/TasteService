package com.firstTaste.crm.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.firstTaste.crm.entity.CustomerInfoEntity;
import com.firstTaste.crm.entity.OrderInfoEntity;
import com.firstTaste.crm.entity.WorkInfoEntity;
import com.firstTaste.crm.repository.OrderInfoRepository;
import com.firstTaste.crm.repository.WorkInfoRepository;
import com.firstTaste.crm.services.iface.ShortMessageService;
import com.firstTaste.crm.services.iface.WorkInfoService;

/**
 * @author yinwenjie
 *
 */
@Service("WorkInfoServiceImpl")
public class WorkInfoServiceImpl implements WorkInfoService {

  @Autowired
  private WorkInfoRepository workInfoRepository;

  @Autowired
  private OrderInfoRepository orderInfoRepository;

  @Autowired
  private ShortMessageService shortMessageService;

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#createByOrderInfo(java. lang.String)
   */
  @Transactional
  @Override
  public WorkInfoEntity createByOrderInfo(String orderInfoId) {
    if (StringUtils.isEmpty(orderInfoId)) {
      throw new IllegalArgumentException("订单编号必须传入!");
    }

    /*
     * 首先只有当前有效的订单，才能进行工单信息的增加 接着，当前工单的配送时间，由上一张工单确认
     */
    // 1、===========
    OrderInfoEntity orderInfo = this.orderInfoRepository.findOne(orderInfoId);
    if (orderInfo == null) {
      throw new IllegalArgumentException("未发现指定的订单信息");
    }
    Integer workNumber = orderInfo.getWorkNumber();
    Integer workExecutedNumber = orderInfo.getWorkExecutedNumber();
    if (workExecutedNumber >= workNumber) {
      throw new IllegalArgumentException("当前订单已经执行完成，不能再进行更改!");
    }
    // 查询客户信息（订购者信息）
    CustomerInfoEntity customerInfo = orderInfo.getOrderer();

    // 2、===========
    List<WorkInfoEntity> workInfos = this.workInfoRepository.findByOrderId(orderInfoId);
    if (workInfos == null || workInfos.isEmpty()) {
      throw new IllegalArgumentException("脏数据!未发现当前订单有任何工单信息!");
    }
    WorkInfoEntity lastWorkInfo = workInfos.get(workNumber - 1);

    // 3、============开始计算新工单的配送时间
    Date lastDeliveryTime = lastWorkInfo.getDeliveryTime();
    Date newlastDeliveryTime = null;
    Calendar calendar = Calendar.getInstance();
    synchronized (calendar) {
      calendar.setTime(lastDeliveryTime);
      calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DATE), 0, 0, 0);
      calendar.add(Calendar.DATE, 7);
      newlastDeliveryTime = calendar.getTime();
    }

    // 4、 =========重新准备当前工单的基本信息
    WorkInfoEntity newLastWorkInfo = new WorkInfoEntity();
    newLastWorkInfo.setAddress(orderInfo.getAddress());
    newLastWorkInfo.setDeliveryTime(newlastDeliveryTime);
    // 这里都填写活动赠送
    newLastWorkInfo.setInfoType(2);
    newLastWorkInfo.setOrderInfo(orderInfo);
    newLastWorkInfo.setPhone(customerInfo.getPhone());
    newLastWorkInfo.setQq(customerInfo.getQq());
    newLastWorkInfo.setWeixin(customerInfo.getWeixin());
    newLastWorkInfo.setStatus(0);

    // 5、==========进行添加
    this.workInfoRepository.saveAndFlush(newLastWorkInfo);

    // 6、==========然后将对应的订单中，冗余汇总的工单数量+1
    this.orderInfoRepository.incrementWorkNumber(orderInfo.getId());
    return newLastWorkInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#doneWorkInfo(java.lang. String)
   */
  @Transactional
  @Override
  public WorkInfoEntity doneWorkInfo(String workInfoId) {
    if (StringUtils.isEmpty(workInfoId)) {
      throw new IllegalArgumentException("工单编号必须传入!");
    }
    /*
     * 完成一张工单的操作包括两步： 1、更改这张工单的状态 2、将工单对应的订单中，已完成工单数量+1
     */
    // 1、=========
    WorkInfoEntity workInfo = this.workInfoRepository.findOne(workInfoId);
    if (workInfo == null) {
      throw new IllegalArgumentException("未发现对应的工单!");
    }
    Integer status = workInfo.getStatus();
    if (status != 0) {
      throw new IllegalArgumentException("这张工单没有处于“未完成”状态，不能进行“完成”操作");
    }
    this.workInfoRepository.doneWorkInfo(workInfo.getId());
    workInfo.setStatus(1);

    // 2、=========
    OrderInfoEntity orderInfo = workInfo.getOrderInfo();
    this.orderInfoRepository.incrementWorkExecutedNumber(orderInfo.getId());

    return workInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#updateRemarkByWorkId( java.lang.String,
   * java.lang.String)
   */
  @Transactional
  @Override
  public void updateRemarkByWorkId(String workInfoId, String remark) {
    if (StringUtils.isEmpty(workInfoId)) {
      throw new IllegalArgumentException("工单编号必须传入!");
    }
    if (StringUtils.isEmpty(remark)) {
      remark = "";
    }
    if (remark.length() > 256) {
      throw new IllegalArgumentException("工单备注必须小于256字符!");
    }

    // 开始进行修改
    this.workInfoRepository.updateRemarkByWorkId(workInfoId, remark);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#
   * updateLogisticsNoByWorkId(java.lang.String, java.lang.String)
   */
  @Transactional
  @Override
  public void updateLogisticsNoByWorkId(String workInfoId, String logisticsNo) {
    if (StringUtils.isEmpty(workInfoId)) {
      throw new IllegalArgumentException("工单编号必须传入!");
    }
    if (StringUtils.isEmpty(logisticsNo)) {
      logisticsNo = "";
    }
    if (logisticsNo.length() > 256) {
      throw new IllegalArgumentException("运单信息必须小于256字符!");
    }

    // 开始进行修改
    this.workInfoRepository.updateLogisticsNoByWorkId(workInfoId, logisticsNo);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#sendMsg(java.lang. String,
   * java.lang.String)
   */
  @Transactional
  @Override
  public void sendMsg(String workInfoId, String mobile) throws IllegalArgumentException {
    /*
     * 发送工单的配送短信，需要如下信息 配送单信息和发送短信的目标电话，处理过程为： 
     * 1、首先判断工单是否存在，判断电话是否存在 如果没有传入电话，则需要从工单上重新找到电话
     * 2、从工单中找到已填写的配送单信息，如果没有填写，则抛出异常 
     * 3、调用第三方短信发送接口，发送信息 
     * 4、更新数据库中的信息，主要是“是否已发送配送单的信息”
     */
    Validate.notEmpty(workInfoId, "工单信息必须传入！");

    // 1、==================
    WorkInfoEntity workInfoEntity = this.workInfoRepository.findByWorkinfoId(workInfoId);
    OrderInfoEntity orderInfo = workInfoEntity.getOrderInfo();
    int totalCount = orderInfo.getWorkNumber();
    int count = orderInfo.getWorkExecutedNumber();
    
    Validate.notNull(workInfoEntity, "没有发现指定的工单信息！");
    String _mobile;
    if (!StringUtils.isEmpty(mobile)) {
      _mobile = mobile;
    } else {
      _mobile = workInfoEntity.getPhone();
    }

    // 2、==================
    String logisticsNo = workInfoEntity.getLogisticsNo();
    Validate.notEmpty(logisticsNo, "要发送配送短信，请首先为工单填写配送信息！");

    // 3、==================
    // 如果条件成立，说明是最后一次配送的短信
    if(count >= totalCount) {
      this.shortMessageService.sendMessageAtLast(logisticsNo, _mobile);
    } else {
      this.shortMessageService.sendMessage(logisticsNo, _mobile , count , totalCount);
    }
    

    // 4、=================
    workInfoEntity.setMsgStatus(1);
    this.workInfoRepository.saveAndFlush(workInfoEntity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#findByOrderId(java.lang .String)
   */
  @Override
  public List<WorkInfoEntity> findByOrderId(String orderInfoId) {
    if (StringUtils.isEmpty(orderInfoId)) {
      throw new IllegalArgumentException("订单编号必须传入!");
    }

    List<WorkInfoEntity> results = this.workInfoRepository.findByOrderId(orderInfoId);
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#findByDeliveryTime(java .util.Date)
   */
  @Override
  public List<WorkInfoEntity> findByDeliveryTime(Date deliveryTime) {
    if (deliveryTime == null) {
      throw new IllegalArgumentException("配送时间必须传入!");
    }

    // 主需要确定年月日就行了
    Calendar calendar = Calendar.getInstance();
    Date currentDeliveryTime = null;
    synchronized (calendar) {
      calendar.setTime(deliveryTime);
      calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DATE), 0, 0, 0);
      currentDeliveryTime = calendar.getTime();
    }

    List<WorkInfoEntity> results = this.workInfoRepository.findByDeliveryTime(currentDeliveryTime);
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#findById(java.lang. String)
   */
  @Override
  public WorkInfoEntity findById(String workInfoId) {
    if (StringUtils.isEmpty(workInfoId)) {
      throw new IllegalArgumentException("工单编号必须传入!");
    }

    WorkInfoEntity result = this.workInfoRepository.findByWorkinfoId(workInfoId);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.firstTaste.crm.services.iface.WorkInfoService#findById(java.lang. String)
   */
  @Override
  public List<WorkInfoEntity> findByConditions(String phone, String weixin, String receiver,
      String receiverPhone, Date beginDeliveryTime, Date endDeliveryTime) {
    if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(weixin) && StringUtils.isEmpty(receiver)
        && StringUtils.isEmpty(receiverPhone) && beginDeliveryTime == null
        && endDeliveryTime == null) {
      throw new IllegalArgumentException("至少输入一种查询方式，作为查询条件!");
    }

    List<WorkInfoEntity> results =
        this.workInfoRepository.findAll(new Specification<WorkInfoEntity>() {
          @Override
          public Predicate toPredicate(Root<WorkInfoEntity> root, CriteriaQuery<?> query,
              CriteriaBuilder cb) {
            List<Predicate> list = new ArrayList<Predicate>();
            root.fetch("orderInfo", JoinType.LEFT);
            // 查询订货人电话
            if (!StringUtils.isEmpty(phone)) {
              list.add(cb.equal(root.get("phone").as(String.class), phone));
            }
            // 查询订货人微信
            if (!StringUtils.isEmpty(weixin)) {
              list.add(cb.equal(root.get("weixin").as(String.class), weixin));
            }
            // 查询收货人名称
            if (!StringUtils.isEmpty(receiver)) {
              list.add(cb.equal(root.get("receiver").as(String.class), receiver));
            }
            // 查询收货人电话
            if (!StringUtils.isEmpty(receiverPhone)) {
              list.add(cb.equal(root.get("receiverPhone").as(String.class), receiverPhone));
            }
            // 查询配送开始时间（包括）
            if (beginDeliveryTime != null) {
              list.add(cb.greaterThanOrEqualTo(root.get("deliveryTime").as(Date.class),
                  beginDeliveryTime));
            }
            // 查询配送结束时间（包括）
            if (endDeliveryTime != null) {
              list.add(
                  cb.lessThanOrEqualTo(root.get("deliveryTime").as(Date.class), endDeliveryTime));
            }
            query.orderBy(cb.asc(root.get("deliveryTime").as(Date.class)));

            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
          }
        });

    return results;
  }
}
