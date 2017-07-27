<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>鲜花管理平台</title>
<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<link rel="stylesheet" type="text/css" href="/stylesheets/theme.css">
<link rel="stylesheet" type="text/css" href="/stylesheets/reveal.css">
<link rel="stylesheet" type="text/css" href="/lib/font-awesome/css/font-awesome.css">
<link rel="stylesheet" type="text/css" href="/lib/bootstrap/css/bootstrap.css">

<script src="/lib/jquery-1.8.1.min.js" type="text/javascript"></script>
<script src="/lib/ajaxfileupload.js" type="text/javascript"></script>
<script src="/lib/jquery-form.js" type="text/javascript"></script>
<script src="/lib/jquery.reveal.js" type="text/javascript"></script>
<!-- Demo page code -->
<style type="text/css">
#line-chart {
	height:300px;
	width:800px;
	margin: 0px auto;
	margin-top: 1em;
}

.brand { font-family: georgia, serif; }
.brand .first {
	color: #ccc;
	font-style: italic;
}

.brand .second {
	color: #fff;
	font-weight: bold;
}
</style>
<script type="text/javascript">
function findWorkInfos() {
	var phone = $("#search_phone").val();
	var weixin = $("#search_weixin").val();
	var receiver = $("#search_receiver").val();
	var receiverPhone = $("#search_receiverPhone").val();
	var beginDeliveryTime = $("#search_begin_deliveryTime").val();
	var endDeliveryTime = $("#search_end_deliveryTime").val();
	
	if(phone == "" && weixin == "" && receiver == ""
		&& receiverPhone == "" && beginDeliveryTime == "" 
		&& endDeliveryTime == "") {
		alert("必须填写至少一个查询条件");
		return;
	}
	
	// 查询用户的订单
	var params = "phone=" + phone + "&weixin=" + weixin + "&receiver=" + receiver + "&receiverPhone=" + receiverPhone + "&beginDeliveryTime=" + beginDeliveryTime + "&endDeliveryTime=" + endDeliveryTime;
	$.ajax({
        type: "GET",
        url: "/workinfos/findByConditions?" + params,
        async: true,
		dataType: "json",
        success: function (listDatas) {
        	buildWorkInfos(listDatas);
        },
        error: function(data) {
        	alert("工单查询出现异常：" + data.message);
        }
    });
}

//构造指定用户下的工单列表信息
function buildWorkInfos(listDatas) {
	//开始构建工单信息
	var tdhtml = "";
	for(var index = 0 ; listDatas != null && index < listDatas.length ; index++) {
		var dataItem = listDatas[index];
		tdhtml += "<tr>";
		tdhtml += "<td align=\"center\">"  + (index+1)  +"</td>";
		// 配送时间
		var deliveryTime = new Date(dataItem.deliveryTime);
		tdhtml += "<td>";
		tdhtml += deliveryTime.getFullYear() + "-" + (deliveryTime.getMonth() + 1) + "-" + deliveryTime.getDate();
		tdhtml += "</td>";
		// 订货人
		var beginTime = new Date(dataItem.beginTime);
		tdhtml += "<td>";
		tdhtml += dataItem.orderer;
		tdhtml += "</td>";
		// 收货人
		var endTime = new Date(dataItem.endTime);
		tdhtml += "<td>";
		tdhtml += dataItem.receiver;
		tdhtml += "</td>";
		// 收货电话
		tdhtml += "<td>"  + dataItem.receiverPhone +"</td>";
		// 订单总配送次数
		tdhtml += "<td>"  + dataItem.orderInfo.workNumber +"</td>";
		// 订单已配送次数
		tdhtml += "<td>"  + dataItem.orderInfo.workExecutedNumber +"</td>";
		// 运单号
		tdhtml += "<td>"  + dataItem.logisticsNo +"</td>";
		// 配送地址
		tdhtml += "<td>"  + dataItem.address +"</td>";
		// 工单备注
		tdhtml += "<td>" + dataItem.remark + "</td>";
		// 工单状态
		tdhtml += "<td>"
		if(dataItem.status == 0) {
			tdhtml += "未完成";
		} else {
			tdhtml += "完成!";
		}
		tdhtml += "</td>";
		// 短信是否发送
		tdhtml += "<td>"
		if(dataItem.msgStatus == 0) {
			tdhtml += "未发送";
		} else {
			tdhtml += "已发送";
		}
		tdhtml += "</td>";
		// 工单完成状态不一样，能进行的操作也不一样
		// 如果条件成立，说明工单未完成
		tdhtml += "<td>";
		if(dataItem.status == 0) {
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preDoneByWorkId('" + dataItem.id + "')\" style=\"margin-right: 10px\">完成</a>";
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preModifyRemarkByWorkId('" + dataItem.id + "','" + dataItem.remark + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myRemarkDiv\">修改备注</a>";
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preModifyLogisticsNoDivByWorkId('" + dataItem.id + "','" + dataItem.logisticsNo + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myLogisticsNoDivs\">录入运单</a>";
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"sendMsgByWorkId('" + dataItem.id + "')\" style=\"margin-right: 10px\">发送短信</a>";
		} else {
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preModifyRemarkByWorkId('" + dataItem.id + "','" + dataItem.remark + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myRemarkDiv\">修改备注</a>";
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preModifyLogisticsNoDivByWorkId('" + dataItem.id + "','" + dataItem.logisticsNo + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myLogisticsNoDivs\">录入运单</a>";
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"sendMsgByWorkId('" + dataItem.id + "')\" style=\"margin-right: 10px\">发送短信</a>";
		}
		tdhtml += "</td>";
		tdhtml += "</tr>";
	}
	
	$("#workInfosBody").html(tdhtml);
}

// 准备进行工单备注信息的修改
function preModifyRemarkByWorkId(workInfoId,remark) {
	$("#workId_Remark").val(workInfoId);
	$("#remark_modify_byid").val(remark);
}

// 准备录入工单，或者更改工单
function preModifyLogisticsNoDivByWorkId(workInfoId,logisticsNo) {
    $("#workId_logisticsNo_Remark").val(workInfoId);
	$("#logisticsNoDiv_modify_byid").val(logisticsNo);
}

// 发送短信
function sendMsgByWorkId(workInfoId) {
    $.ajax({
		type: "POST",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/workinfos/sendMsg/" + workInfoId, 
		dataType: "json",
		success: function(dataItem) {
			alert("配送短信发送完成！");
			findWorkInfos();
		},
		error: function(dataItem) {
			alert("配送短信发送异常，可能是没有填写运单号！")
		}
	});
}

// 准备完成一张工单
// TODO 应该记录完成人
function preDoneByWorkId(workInfoId) {
	$.ajax({
		type: "POST",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/workinfos/done/" + workInfoId, 
		dataType: "json",
		success: function(dataItem) {
			alert("工单完成！");
			findWorkInfos();
		}
	});
}

// 正在进行工单备注信息修改
function modifyRemarkByWorkId() {
	var workInfoId = $("#workId_Remark").val();
	var remark = $("#remark_modify_byid").val();
	if(remark == "") {
		remark = "无";
	}
	
	// 修改订单信息
	$.ajax({
		type: "POST",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/workinfos/updateRemark/" + workInfoId + "/" + remark , 
		dataType: "json",
		success: function(dataItem) {
			alert("订单备注信息修改完成");
			findWorkInfos();
		}
	});
}

// 正在进行工单的运单信息修改
function modifyLogisticsNoByWorkId() {
    var workInfoId = $("#workId_logisticsNo_Remark").val();
	var logisticsNo = $("#logisticsNoDiv_modify_byid").val();
	if(logisticsNo == "") {
		logisticsNo = "";
	}
	
	// 修改订单信息——运单
	$.ajax({
		type: "POST",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/workinfos/logisticsNo/" + workInfoId + "/" + logisticsNo , 
		dataType: "json",
		success: function(dataItem) {
			alert("订单-运单信息修改完成");
			findWorkInfos();
		}
	});
}

// 对工单信息进行修改
function modifyWorkByWorkId() {
	var customerId =  $("#customerId_modify").val();
	var orderId = $("#orderId_modify").val();
	var ablePay = $("#ablePay_modify").val();
	var realPay = $("#realPay_modify").val();
	var receiver = $("#receiver_modify").val();
	var address = $("#address_modify").val();
	var receiverPhone = $("#receiverPhone_modify").val();
	var remark = $("#remark_modify").val();
	
	var goodForm = true;
	var error = "";
	if(ablePay == "") {
		goodForm = false;
		error += "应收金额必须填写！<br>";
	}
	if(realPay == "") {
		goodForm = false;
		error += "实收金额必须填写！<br>";
	}
	if(address == "") {
		goodForm = false;
		error += "送货地址必须填写！<br>";
	}
	if(receiver == "") {
		goodForm = false;
		error += "收货人必须填写！<br>";
	}
	if(receiverPhone == "") {
		goodForm = false;
		error += "收货人电话必须填写！<br>";
	}
	if(!goodForm) {
		alert(error);
		return;
	}
	
	// 构造结构，准备进行订单基本信息更新
	var json = {
			"orderer":{
				"id":customerId
			},
			"id":orderId,
			"address":address,
			"receiver":receiver,
			"receiverPhone":receiverPhone,
			"remark": remark,
			"ablePay": ablePay,
			"realPay": realPay
		};
		$.ajax({
	        type: "PATCH",
	        url: "/v1/orderinfos",
	        contentType: "application/json; charset=utf-8",
	        async: true,
	        data: JSON.stringify(json),
			dataType: "json",
	        success: function (data) {
	        	alert("订单基本信息修改完成，订单编号：" + data.id);
	        },
	        error: function(data) {
	        	alert("订单基本信息修改出现问题，订单编号：" + data.message);
	        }
	    });
}
</script>
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
	<script src="/javascripts/html5.js"></script>
<![endif]-->
<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="/lib/font-awesome/docs/assets/ico/favicon.ico">
<link rel="apple-touch-icon-precomposed" href="/lib/font-awesome/docs/assets/ico/apple-touch-icon-144-precomposed.png">
<link rel="apple-touch-icon-precomposed" href="/lib/font-awesome/docs//assets/ico/apple-touch-icon-114-precomposed.png">
<link rel="apple-touch-icon-precomposed" href="/lib/font-awesome/docs//assets/ico/apple-touch-icon-72-precomposed.png">
<link rel="apple-touch-icon-precomposed" href="/lib/font-awesome/docs//assets/ico/apple-touch-icon-57-precomposed.png">
</head>

<!--[if lt IE 7 ]> <body class="ie ie6"> <![endif]-->
<!--[if IE 7 ]> <body class="ie ie7"> <![endif]-->
<!--[if IE 8 ]> <body class="ie ie8"> <![endif]-->
<!--[if IE 9 ]> <body class="ie ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> 
<body>
<!--<![endif]-->

<div class="navbar">
	<#include "/common/userbar.ftl">
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<#include "/common/mainmenu.ftl">
		
		<!-- 列表 -->
		<div class="span9">
			<!-- 列表头 -->
			<h1 class="page-title">工单信息</h1>
			
			<div class="well" style="float: left; min-width: 900px; margin-bottom: 5px ; padding-bottom: 10px">
				<label><b>筛选：</b>——工单信息：</label> 
				<label style="float: left; padding-right: 10px">订货人电话：<input type="text" id="search_phone" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">订货人微信：<input type="text" id="search_weixin" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">收货人：<input type="text" id="search_receiver" value="" style="width: 160px; height: 30px"/></label>
				<label style="padding-right: 10px">
					<a href="javascript:void(0);"  id="searchbutton" onclick="findWorkInfos()" class="btn "><i class="icon-save"></i> Search</a>
				</label>
				<label style="float: left; padding-right: 10px">收货人电话：<input type="text" id="search_receiverPhone" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">配送起始时间（包括）：<input type="text" id="search_begin_deliveryTime" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">配送截至时间（包括）：<input type="text" id="search_end_deliveryTime" value="" style="width: 160px; height: 30px"/></label>
			</div>
			
			<!-- 列表正文 -->
			<div class="well">
				<table class="table">
					<thead>
						<tr>
							<th>#</th>
							<th>工单配送时间</th>
							<th>订货人</th>
							<th>收货人</th>
							<th>收货电话</th>
							<th>订单总次数</th>
							<th>点单已配送次数</th>
							<th>运单号</th>
							<th>配送地址</th>
							<th>工单备注</th>
							<th>完成状态</th>
							<th>短信发送</th>
							<th style="width: 100px;">操作</th>
						</tr>
					</thead>
					<tbody id="workInfosBody">
						
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<!-- Le javascript
	================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/lib/bootstrap/js/bootstrap.js"></script>
	
	<!-- 工单修改页面myModifyWorkInfos -->
	<div id="myModifyWorkInfos" class="reveal-modal" style="width: 280px;height: 780px;">
		<input type="hidden" id="customerId_modify" name="customerId_modify" value="false"/>
		<input type="hidden" id="orderId_modify" name="orderId_modify" value="false"/>
		<label style="padding: 5px;margin: 0px;">订货人：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="orderer_modify"  readonly="readonly" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="40"/>
		</label>
		<label style="padding: 5px;margin: 0px;">商品信息：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="commodityName_modify"  readonly="readonly" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
		</label>
		<label style="padding: 5px;margin: 0px;">订单首次执行时间：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="beginTime_modify"  readonly="readonly" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
		</label>
		<label style="padding: 5px;margin: 0px;">订单执行次数：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="workNumber_modify"  readonly="readonly"  style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
		</label>
		<label style="padding: 5px;margin: 0px;">订单已执行次数：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="workExecutedNumber_modify"  readonly="readonly"  style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
		</label>
		<label style="padding: 5px;margin: 0px;">应付金额：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="ablePay_modify" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">实付金额：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="realPay_modify" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">收货人：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="receiver_modify" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">收货人地址：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="address_modify" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">收货人电话：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="receiverPhone_modify" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">订单备注：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="remark_modify" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="modifyOrderByOrderId()" id="modifyOrderButton" class="btn btn-primary"><i class="icon-save"></i> Update </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>

	<!-- 工单备注信息改变 myRemarkDiv -->
	<div id="myRemarkDiv" class="reveal-modal" style="width: 240px;height: 120px;">
		<input type="hidden" id="workId_Remark"/>
		<label style="padding: 5px;margin: 0px;">订单备注：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="remark_modify_byid"  style="width: 210px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="120"/>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="modifyRemarkByWorkId()" id="modifyRemarkByWorkIdButton" class="btn btn-primary"><i class="icon-save"></i> Update </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>
	
	<!-- 运单信息录入myLogisticsNoDivs -->
	<div id="myLogisticsNoDivs" class="reveal-modal" style="width: 240px;height: 120px;">
		<input type="hidden" id="workId_logisticsNo_Remark"/>
		<label style="padding: 5px;margin: 0px;">运单信息：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="logisticsNoDiv_modify_byid"  style="width: 210px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="120"/>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="modifyLogisticsNoByWorkId()" id="modifyLogisticsNoByWorkIdButton" class="btn btn-primary"><i class="icon-save"></i> Update </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>
</div>
</body>
</html>