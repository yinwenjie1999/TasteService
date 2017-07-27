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
//构造指定用户下的订单列表信息
function buildOrderInfos(listDatas) {
	//开始构建订单信息
	var tdhtml = "";
	for(var index = 0 ; listDatas != null && index < listDatas.length ; index++) {
		var dataItem = listDatas[index];
		tdhtml += "<tr>";
		tdhtml += "<td align=\"center\">"  + (index+1)  +"</td>";
		// 编号后10位
		tdhtml += "<td align=\"center\">" + dataItem.id.substring(25) + "</td>";
		// 下单时间
		var createTime = new Date(dataItem.createTime);
		tdhtml += "<td>";
		tdhtml += createTime.getFullYear() + "-" + (createTime.getMonth() + 1) + "-" + createTime.getDate();
		tdhtml += "</td>";
		// 首送时间
		var beginTime = new Date(dataItem.beginTime);
		tdhtml += "<td>";
		tdhtml += beginTime.getFullYear() + "-" + (beginTime.getMonth() + 1) + "-" + beginTime.getDate();
		tdhtml += "</td>";
		// 末送时间
		var endTime = new Date(dataItem.endTime);
		tdhtml += "<td>";
		tdhtml += endTime.getFullYear() + "-" + (endTime.getMonth() + 1) + "-" + endTime.getDate();
		tdhtml += "</td>";
		// 工单总次数
		tdhtml += "<td>"  + dataItem.workNumber  +"</td>";
		// 工单已执行次数
		tdhtml += "<td>"  + dataItem.workExecutedNumber  +"</td>";
		// 商品信息
		tdhtml += "<td>" + dataItem.receiver + "</td>";
		// 应付与实付
		tdhtml += "<td>" + dataItem.realPay  + "/" + dataItem.ablePay + "</td>";
		// 操作
		tdhtml += "<td align=\"center\">";
		tdhtml += "<a href=\"javascript:void(0);\" onclick=\"findWorkByOrderId('" + dataItem.id + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myWorkInfosDivs\">看工单</a>";
		tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preModifyOrderId('" + dataItem.orderer.id  + "' , '" + dataItem.id + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myModifyOrderInfosDiv\">修改信息</a>";
		// 只有订单没有执行完，才能进行次数修改和进行时间变更操作
		if(dataItem.workExecutedNumber < dataItem.workNumber) {
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preWorkNumberChange('" + dataItem.id + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myWorkNumberChangeDiv\">修改次数</a>";
			tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preWorkTimeDelay('" + dataItem.id + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myWorkTimeDelayDiv\">改期</a>";
		}
		tdhtml += "</td>";
		tdhtml += "</tr>";
	}
	
	$("#orderinfosBody").html(tdhtml);
}

// 查询这个订单下的工单信息，按配送时间
function findWorkByOrderId(orderId) {
	$.ajax({
        type: "GET",
        url: "/v1/workinfos/" + orderId,
        async: true,
		dataType: "json",
        success: function (listDatas) {
        	buildWorkInfos(listDatas);
        }
    });
}

// 构建工单列表
function buildWorkInfos(listDatas) {
	//开始构建订单信息
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
		// 工单性质
		var infoType = dataItem.infoType;
		tdhtml += "<td>";
		if(infoType == 1) {
			tdhtml += "自动生成";
		} else {
			tdhtml += "活动赠送";
		}
		tdhtml += "</td>";
		// 订货人
		tdhtml += "<td>";
		tdhtml += dataItem.orderer;
		tdhtml += "</td>";
		// 收货人
		tdhtml += "<td>"  + dataItem.receiver  +"</td>";
		// 收货人电话
		tdhtml += "<td>"  + dataItem.receiverPhone  +"</td>";
		// 配送地址
		tdhtml += "<td>" + dataItem.address + "</td>";
		// 完成情况
		if(dataItem.status == 0) {
			tdhtml += "<td>未完成</td>";
		} else {
			tdhtml += "<td>完成！</td>";
		}
		tdhtml += "</tr>";
	}
	
	$("#myWorkInfosDiv").html(tdhtml);
} 

//按照用户编号，查询这个用户下的订单信息
function findOrderInfos() {
	var orderids = $("#search_orderids").val();
	var phone = $("#search_phone").val();
	var weixin = $("#search_weixin").val();
	var receiver = $("#search_receiver").val();
	var receiverPhone = $("#search_receiverPhone").val();
	if(orderids == "" && phone == "" && weixin == ""
			&& receiver == "" && receiverPhone == "") {
		alert("必须填写至少一个查询条件");
		return;
	}
	
	// 查询用户的订单
	var params = "orderids=" + orderids + "&phone=" + phone + "&weixin=" + weixin + "&receiver=" + receiver + "&receiverPhone=" + receiverPhone;
	$.ajax({
        type: "GET",
        url: "/orderinfos/findByConditions?" + params,
        async: true,
		dataType: "json",
        success: function (listDatas) {
        	buildOrderInfos(listDatas);
        },
        error: function(data) {
        	alert("订单查询出现问题：" + data.message);
        }
    });
} 

// 准备进行订单信息的修改
function preModifyOrderId(customerId , orderId) {
	$("#customerId_modify").val(customerId);
	$("#orderId_modify").val(orderId);
	$("#orderer_modify").val("");
	$("#beginTime_modify").val("");
	$("#workNumber_modify").val("");
	$("#ablePay_modify").val("");
	$("#realPay_modify").val("");
	$("#address_modify").val("");
	$("#receiver_modify").val("");
	$("#receiverPhone_modify").val("");
	$("#remark_modify").val("");
	
	// 查询订货人姓名
	$.get("/v1/customers/getOne/" + customerId,function(data,status){
		$("#orderer_modify").html(data.name);
	  });
	
	// 查询订单信息
	$.ajax({
		type: "GET",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/orderinfos/One/" + orderId , 
		dataType: "json",
		success: function(dataItem) {
			// 订货人
			$("#orderer_modify").val(dataItem.orderer.name);
			$("#orderId_modify").val(dataItem.id);
			// 商品信息（不能修改）
			$("#commodityName_modify").val(dataItem.commodity.name)
			// 首送时间不能修改
			var beginTime = new Date(dataItem.beginTime);
			$("#beginTime_modify").val(beginTime.getFullYear() + "-" + (beginTime.getMonth() + 1) + "-" + beginTime.getDate());
			// 订单次数不能修改
			$("#workNumber_modify").val(dataItem.workNumber);
			// 订单已执行次数，不能修改
			$("#workExecutedNumber_modify").val(dataItem.workExecutedNumber);
			// 应付金额
			$("#ablePay_modify").val(dataItem.ablePay);
			// 实付金额
			$("#realPay_modify").val(dataItem.realPay);
			// 送货地址
			$("#address_modify").val(dataItem.address);
			// 收货人
			$("#receiver_modify").val(dataItem.receiver);
			// 收货人电话
			$("#receiverPhone_modify").val(dataItem.receiverPhone);
			// 备注
			$("#remark_modify").val(dataItem.remark);
		}
	});
}

// 对订单信息进行修改
function modifyOrderByOrderId() {
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

// 订单未送工单批量改期
function preWorkTimeDelay(orderId) {
	// 查询订单信息
	$.ajax({
		type: "GET",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/orderinfos/One/" + orderId , 
		dataType: "json",
		success: function(dataItem) {
			// 订单Id
			$("#orderId_Delaymodify").val(dataItem.id);
			// 首送时间不能修改
			$("#nextTime_delay").val("");
		}
	});
}

// 进行指定的工单改期操作（只对未执行的工单批量改期）
function workTimeDelayByOrderId() {
	var orderId = $("#orderId_Delaymodify").val();
	var nextTime = $("#nextTime_delay").val();
	
	// 对日期格式进行判断，正则判断
	if(nextTime == "") {
		alert("新的送货日期，必须填写!");
		return;
	}
	
	// 进行更新
	$.ajax({
		type: "PATCH",
		async: true , 
		url: "/v1/orderinfos/delayWorkTime/" + orderId + "/" + nextTime,
		success: function(dataItem) {
			alert("工单延期操作成功！最近一次计划配送时间为：" + nextTime);
		},
		error: function() {
			alert("工单延期操作出现问题！")
		}
	});
}

// 修改工单数量前的准备操作
function preWorkNumberChange(orderId) {
	// 查询订单信息
	$.ajax({
		type: "GET",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/orderinfos/One/" + orderId , 
		dataType: "json",
		success: function(dataItem) {
			// 订单Id
			$("#orderId_workNumbermodify").val(dataItem.id);
			// 首送时间不能修改
			$("#workNumber_change").val("");
		}
	});
}

// 开始进行工单次数的变更
function workNumberChange() {
	var orderId = $("#orderId_workNumbermodify").val();
	var workNumber = $("#workNumber_change").val();
	
	// 对日期格式进行判断，正则判断
	if(workNumber == "") {
		alert("新的工单次数，必须填写!");
		return;
	}
	
	$.ajax({
		type: "PATCH",
		async: true , 
		url: "/v1/orderinfos/updateOrderWorkNumber/" + orderId + "/" + workNumber,
		success: function(dataItem) {
			alert("工单数量操作成功！新的总配送数量为：" + workNumber);
		},
		error: function() {
			alert("工单数量操作出现问题！")
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
			<h1 class="page-title">订单信息</h1>
			
			<div class="well" style="float: left; min-width: 900px; margin-bottom: 5px ; padding-bottom: 10px">
				<label><b>筛选：</b>——订单信息：</label> 
				<label style="float: left; padding-right: 10px">订单编号：<input type="text" id="search_orderids" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">客户电话：<input type="text" id="search_phone" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">客户微信：<input type="text" id="search_weixin" value="" style="width: 160px; height: 30px"/></label>
				<label style="padding-right: 10px">
					<a href="javascript:void(0);"  id="searchbutton" onclick="findOrderInfos()" class="btn "><i class="icon-save"></i> Search</a>
				</label>
				<label style="float: left; padding-right: 10px">收货人：<input type="text" id="search_receiver" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">收货人电话：<input type="text" id="search_receiverPhone" value="" style="width: 160px; height: 30px"/></label>
			</div>
			
			<!-- 列表正文 -->
			<div class="well">
				<table class="table">
					<thead>
						<tr>
							<th>#</th>
							<th>编号后10位</th>
							<th>下单时间</th>
							<th>首送时间</th>
							<th>末送时间</th>
							<th>工单次数</th>
							<th>已执行次数</th>
							<th>收货人</th>
							<th>实付/应付</th>
							<th style="width: 100px;">操作</th>
						</tr>
					</thead>
					<tbody id="orderinfosBody">
						
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<!-- Le javascript
	================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/lib/bootstrap/js/bootstrap.js"></script>
	
	<!-- 工单列表myWorkInfosDivs -->
	<div id="myWorkInfosDivs" class="reveal-modal" style="width: 900px;height: 420px; overflow-x: hidden; overflow-y: hidden">
		<font color="#999999" id="myWorkInfosDivs_orderIds"></font><br/>
		<table class="table">
			<thead>
				<tr>
					<th>#</th>
					<th>配送时间</th>
					<th>工单性质</th>
					<th>订货人</th>
					<th>收货人</th>
					<th>收货人电话</th>
					<th>配送地址</th>
					<th>完成情况</th>
				</tr>
			</thead>
			<tbody id="myWorkInfosDiv">
				
			</tbody>
		</table>
		<a class="close-reveal-modal">×</a>
	</div>
	
	<!-- 工单创建页面myModifyOrderInfos -->
	<div id="myModifyOrderInfosDiv" class="reveal-modal" style="width: 280px;height: 800px;">
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

	<!-- 订单未送工单改期 myWorkTimeDelayDiv -->
	<div id="myWorkTimeDelayDiv" class="reveal-modal" style="width: 280px;height: 120px;">
		<input type="hidden" id="orderId_Delaymodify" name="orderId_Delaymodify"/>
		<label style="padding: 5px;margin: 0px;">订单下次执行时间：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="nextTime_delay"  style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
			<font style="color:#FF0000"> 格式为（yyyy-MM-dd） </font>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="workTimeDelayByOrderId()" id="modifyWorkTimeDelayButton" class="btn btn-primary"><i class="icon-save"></i> Update </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>
	
	<!-- 订单数量改变 myWorkNumberChangeDiv -->
	<div id="myWorkNumberChangeDiv" class="reveal-modal" style="width: 280px;height: 120px;">
		<input type="hidden" id="orderId_workNumbermodify" name="orderId_workNumbermodify" />
		<label style="padding: 5px;margin: 0px;">请输入订单总数量：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="workNumber_change"  style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="workNumberChange()" id="workNumberChangeButton" class="btn btn-primary"><i class="icon-save"></i> Update </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>
</div>
</body>
</html>