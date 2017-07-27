package com.firstTaste.crm.controller.restful;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.firstTaste.crm.controller.BasicController;
import com.firstTaste.crm.entity.OrderInfoEntity;
import com.firstTaste.crm.entity.WorkInfoEntity;
import com.firstTaste.crm.services.iface.WorkInfoService;

/**
 * @author yinwenjie
 */
@RestController
@RequestMapping("/v1/workinfos")
public class WorkInfoController extends BasicController {
	
	@Autowired
	private WorkInfoService workInfoService;
	
	/**
	 * 通过指定的订单编号，查询对应的若干订单信息
	 * @param orderInfoId 指定的订单信息
	 * @return
	 */
	@RequestMapping(value = "/{orderInfoId}", method = RequestMethod.GET)
	public List<WorkInfoEntity> findByOrderId(@PathVariable("orderInfoId") String orderInfoId) {
		List<WorkInfoEntity> results = this.workInfoService.findByOrderId(orderInfoId);
		// 把结果中关联的订单信息去掉，免得引起不必要的错误
		for (WorkInfoEntity workInfo : results) {
			workInfo.setOrderInfo(null);
		}
		return results;
	}
	
	/**
	 * 查询指定日期需要进行配送的工单信息（只有工单的详细信息）
	 * @param deliveryTime
	 * @return
	 */
	@RequestMapping(value = "/findByDeliveryTime/{deliveryTime}", method = RequestMethod.GET)
	public List<WorkInfoEntity> findByDeliveryTime(@PathVariable("deliveryTime") String deliveryTime) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date deliveryTimedate = dateFormat.parse(deliveryTime);
		
		List<WorkInfoEntity> results = this.workInfoService.findByDeliveryTime(deliveryTimedate);
		if(results == null || results.isEmpty()) {
			return Collections.emptyList();
		}
		
		// 把结果中关联的订单信息去掉，免得引起不必要的错误
		for (WorkInfoEntity workInfo : results) {
			workInfo.setOrderInfo(null);
		}
		return results;
	}
	
	/**
	 * 查询一个指定的工单，和它关联的订单信息，但是订单关联的信息就不再进行显示了
	 * @param workInfoId 指定的工单信息
	 * @return
	 */
	@RequestMapping(value = "/getone/{workInfoId}", method = RequestMethod.GET)
	public WorkInfoEntity findById(@PathVariable("workInfoId") String workInfoId) {
		WorkInfoEntity  result = this.workInfoService.findById(workInfoId);
		// 工单对应的订单信息也要查询，但是订单关联的信息，就没必要查了
		OrderInfoEntity orderInfo = result.getOrderInfo();
		
		orderInfo.setCommodity(null);
		orderInfo.setOrderer(null);
		orderInfo.setIntroducer(null);
		orderInfo.setSupplierChannel(null);
		orderInfo.setWorkInfos(null);
		return result;
	}
	
	/**
	 * 创建工单信息——脱离订单单独创建一张工单。注意，这张工单信息以赠送的方式存在
	 * @param customerInfo
	 * @return 
	 */
	@RequestMapping(value = "{orderInfoId}", method = RequestMethod.POST)
	public WorkInfoEntity createByOrderInfo(@PathVariable("orderInfoId")  String orderInfoId) {
		WorkInfoEntity workinfo =  this.workInfoService.createByOrderInfo(orderInfoId);
		// 免得产生关联查询——在事务外转换json时
		workinfo.setOrderInfo(null);
		return workinfo;
	}
	
	/**
	 * 修改指定工单的备注信息
	 * @return 
	 */
	@RequestMapping(value = "/updateRemark/{workInfoId}/{remark}", method = RequestMethod.POST)
	public WorkInfoEntity updateRemarkByWorkId(@PathVariable("workInfoId") String workInfoId , @PathVariable("remark") String remark) {
		this.workInfoService.updateRemarkByWorkId(workInfoId, remark);
		WorkInfoEntity workInfo = this.workInfoService.findById(workInfoId);
		workInfo.setOrderInfo(null);
		
		return workInfo;
	}
	
	/**
	 * 修改指定工单的运单信息
	 * @return 
	 */
	@RequestMapping(value = "/logisticsNo/{workInfoId}/{logisticsNo}", method = RequestMethod.POST)
	public WorkInfoEntity updateLogisticsNoByWorkId(@PathVariable("workInfoId") String workInfoId , @PathVariable("logisticsNo") String logisticsNo) {
		this.workInfoService.updateLogisticsNoByWorkId(workInfoId, logisticsNo);
		WorkInfoEntity workInfo = this.workInfoService.findById(workInfoId);
		workInfo.setOrderInfo(null);
		
		return workInfo;
	}
	
	/**
	 * 完成一个工单，并返回这个工单信息（不包括工单关联的订单信息）
	 * @param workInfoId
	 * @return 
	 */
	@RequestMapping(value = "/done/{workInfoId}", method = RequestMethod.POST)
	public WorkInfoEntity doneWorkInfo(@PathVariable("workInfoId")  String workInfoId) {
		WorkInfoEntity doneWork = this.workInfoService.doneWorkInfo(workInfoId);
		// 免得产生关联查询——在事务外转换json时
		doneWork.setOrderInfo(null);
		return doneWork;
	}

	/**
	 * 发送短信——对于指定的工单
	 * @param workInfoId 指定的工单信息
	 * @param mobile 发送的手机号，可以不传入，如果不传入则默认使用工单的收货电话
	 */
	@RequestMapping(value={"/sendMsg/{workinfoId}"} , method=RequestMethod.POST)
	public void sendMsg(@PathVariable("workinfoId") String workinfoId) throws ParseException {
		this.workInfoService.sendMsg(workinfoId, "");
	}
}
