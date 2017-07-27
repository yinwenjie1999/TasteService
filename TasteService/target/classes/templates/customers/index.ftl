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
$(document).ready(function () {
	queryCustomersAndStore();
});

// 查询当前的用户信息
function queryCustomersAndStore() {
	$.get("/v1/customers",function(listDatas,status){
		buildCustomers(listDatas);
	  });
}

// 刷新客户列表页面
function refreshCustomers() {
	queryCustomersAndStore();
}

// 创建客户信息列表（1000人左右都能支持）
function buildCustomers(listDatas) {
	//开始构建客户信息
	var tdhtml = "";
	for(var index = 0 ; listDatas != null && index < listDatas.length ; index++) {
		var dataItem = listDatas[index];
		tdhtml += "<tr>";
		tdhtml += "<td align=\"center\">"  + (index+1)  +"</td>";
		// 姓名
		tdhtml += "<td>";
		tdhtml += dataItem.name;
		tdhtml += "</td>";
		// 联系方式
		tdhtml += "<td align=\"center\" style=\"width: 200px\">";
		tdhtml += "[phone:" + dataItem.phone + "]</br>"
		tdhtml += "[weixin:" + dataItem.weixin + "]</br>"
		tdhtml += "[qq:" + dataItem.qq + "]"
		tdhtml += "</td>";
		// 加入时间
		var createTime = new Date(dataItem.createTime);
		tdhtml += "<td align=\"center\" style=\"width: 200px\">";
		tdhtml += createTime.getFullYear() + "-" + (createTime.getMonth() + 1) + "-" + createTime.getDate();
		tdhtml += "</td>";
		// 操作
		tdhtml += "<td align=\"center\">";
		tdhtml += "<a href=\"javascript:void(0);\" onclick=\"findOrderByCustomer('" + dataItem.id + "')\" style=\"margin-right: 10px\" data-reveal-id=\"myOrderInfos\">订单列表</a>";
		tdhtml += "<a href=\"javascript:void(0);\" onclick=\"preCreateOrderBuCustomerId('" + dataItem.id + "')\" style=\"margin-right: 10px\" data-reveal-id=\"craeteOrderByCustomerDiv\">创建订单</a>";
		tdhtml += "</td>";
		tdhtml += "</tr>";
	}
	
	$("#customersBody").html(tdhtml);
}

// 初始化客户添加页面
function preAppendCustomer() {
	$("#repeatusername_create").val("false");
	$("#name_craete").val("");
	$("#weixin_craete").val("");
	$("#weixinSubscribe_craete").val("");
	$("#phone_craete").val("");
	$("#qq_craete").val("");
}

//创建客户
function createUser() {
	var name = $("#name_craete").val();
	var weixin = $.trim($("#weixin_craete").val());
	var weixinSubscribe = $.trim($("#weixinSubscribe_craete").val());
	var phone = $("#phone_craete").val();
	var qq = $("#qq_craete").val();	
	
	//=======首先验证填写的内容
	var goodForm = true;
	var error = "";
	if(name == "") {
		goodForm = false;
		error += "请填写客户真实姓名（姓都行）！<br>";
	}
	if(phone == "") {
		goodForm = false;
		error += "电话必须填写，不然配送环节会出问题！<br>";
	}
	
	if(!goodForm) {
		alert(error);
		return;
	}
	
	//======构建提交数据结构，准备提交
	var json = {
		"name":name,
		"weixin":weixin,
		"phone":phone,
		"qq":qq
	};
	$.ajax({
		type: "POST",
		async: true, 
		contentType: "application/json; charset=utf-8",
		url: "/v1/customers", 
		data: JSON.stringify(json),
		dataType: "json",
		success: function(data) {
			alert("添加成功，您可以继续添加，也可以退出该对话框!");
		}
	});
}

// 查询指定的客户
function searchCustomers() {
	// 联系方式至少要填写一个
	var search_phone = $("#search_phone").val();
	var search_weixin = $("#search_weixin").val();
	var search_qq = $("#search_qq").val();
	if(search_phone == "" && search_weixin == "" && search_qq == "") {
		alert("必须至少输入一种联系方式!");
		return;
	}
	
	var conditions = "phone=" + search_phone + "&weixin=" + search_weixin + "&qq=" + search_qq
	$.get("/v1/customers/conditions?" + conditions,function(listDatas,status) {
		buildCustomers(listDatas);
	  });
}

// 按照用户编号，查询这个用户下的订单信息
function findOrderByCustomer(customerId) {
	// 查询用户基本信息
	$.get("/v1/customers/getOne/" + customerId,function(data,status){
		$("#myOrderInfos_customername").html(data.name);
	  });
	
	// 查询用户的订单
	$.get("/v1/orderinfos/" + customerId,function(listDatas,status){
		buildOrderInfos(listDatas);
	  });
}

// 构造指定用户下的订单列表信息
function buildOrderInfos(listDatas) {
	//开始构建客户信息
	var tdhtml = "";
	for(var index = 0 ; listDatas != null && index < listDatas.length ; index++) {
		var dataItem = listDatas[index];
		tdhtml += "<tr>";
		tdhtml += "<td align=\"center\">"  + (index+1)  +"</td>";
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
		tdhtml += "<td>" + dataItem.commodity.name + "</td>";
		// 应付与实付
		tdhtml += "<td>" + dataItem.realPay  + "/" + dataItem.ablePay + "</td>";
		tdhtml += "</tr>";
	}
	
	$("#myOrderInfosDiv").html(tdhtml);
}

// 根据客户Id信息，进行订单创建前，需要首先准备
function preCreateOrderBuCustomerId(customerId) {
	var customer = null;
	$.ajax({
        type: "GET",
        url: "/v1/customers/getOne/" + customerId,
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function (message) {
        	customer = message;
        },
    });
	
	$("#customerId_create").val(customerId);
	$("#orderer_craete").val(customer.name);
	$("#beginTime_craete").val("");
	$("#workNumber_craete").val("4");
	$("#ablePay_craete").val("88.50");
	$("#realPay_craete").val("0.00");
	$("#receiver_craete").val(customer.name);
	$("#address_craete").val("");
	$("#receiverPhone_craete").val(customer.phone);
	$("#remark_craete").val("");
}

// 未指定的客户添加一个订单
function createOrderByCustomerId() {
	var customerId = $("#customerId_create").val();
	var orderer = $("#orderer_craete").val();
	var commodity = $("#commodity_craete").val();
	var beginTime = $.trim($("#beginTime_craete").val());
	var workNumber = $.trim($("#workNumber_craete").val());
	var ablePay = $("#ablePay_craete").val();
	var realPay = $("#realPay_craete").val();
	var address = $("#address_craete").val();
	var receiver = $("#receiver_craete").val();
	var receiverPhone = $("#receiverPhone_craete").val();
	var remark = $("#remark_craete").val();
	
	//=======首先验证填写的内容
	var goodForm = true;
	var error = "";
	if(orderer == "") {
		goodForm = false;
		error += "订货人必须填写（姓都行）！<br>";
	}
	if(commodity == "") {
		goodForm = false;
		error += "商品必须选择！<br>";
	}
	if(beginTime == "") {
		goodForm = false;
		error += "首次配送时间必须填写！<br>";
	}
	if(workNumber == "") {
		goodForm = false;
		error += "配送次数必须填写！<br>";
	}
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
	
	// ========构建json结构，准备向服务器请求
	var json = {
		"orderer":{
			"id":customerId
		},
		"commodity":{
			"id":commodity
		},
		"address":address,
		"beginTime":beginTime,
		"receiver":receiver,
		"receiverPhone":receiverPhone,
		"workNumber": workNumber,
		"workExecutedNumber": 0,
		"remark": remark,
		"ablePay": ablePay,
		"realPay": realPay
	};
	$.ajax({
        type: "POST",
        url: "/v1/orderinfos",
        contentType: "application/json; charset=utf-8",
        async: true,
        data: JSON.stringify(json),
		dataType: "json",
        success: function (data) {
        	alert("订单添加完成，订单编号：" + data.id);
        },
        error: function(data) {
        	alert("订单添加出现问题，订单编号：" + data.message);
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
			<h1 class="page-title">客户信息</h1>
			
			<div class="well" style="float: left; min-width: 900px; margin-bottom: 5px ; padding-bottom: 10px">
				<label><b>筛选：</b></label> 
				<label style="float: left; padding-right: 10px">电话：<input type="text" id="search_phone" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">微信：<input type="text" id="search_weixin" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">QQ：<input type="text" id="search_qq" value="" style="width: 160px; height: 30px"/></label>
				<label style="float: left; padding-right: 10px">
					<a href="javascript:void(0);"  id="searchbutton" onclick="searchCustomers()" class="btn "><i class="icon-save"></i> Search</a>
					<a href="javascript:void(0);" id="perAppendCustomerbutton" onclick="preAppendCustomer()" class="btn btn-primary" data-reveal-id="craeteUserDiv"> 创建客户</a>
					<a href="javascript:void(0);"  id="refreshButton" onclick="refreshCustomers()" class="btn "> 刷新</a>
				</label>
			</div>
			
			<!-- 列表正文 -->
			<div class="well">
				<table class="table">
					<thead>
						<tr>
							<th>#</th>
							<th>客户姓名</th>
							<th>联系方式</th>
							<th>加入时间</th>
							<th style="width: 100px;">操作</th>
						</tr>
					</thead>
					<tbody id="customersBody">
						
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<!-- Le javascript
	================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/lib/bootstrap/js/bootstrap.js"></script>
	
	<!-- 客户创建/编辑页面 -->
	<div id="craeteUserDiv" class="reveal-modal" style="width: 280px;height: 420px;">
		<input type="hidden" id="repeatusername_create" name="repeatusername_create" value="false"/>
		<label style="padding: 5px;margin: 0px;">真实姓名：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="name_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="40"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">微信号：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="weixin_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
		</label>
		<label style="padding: 5px;margin: 0px;">微信订阅号：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="weixinSubscribe_craete"  readonly="readonly" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="40"/>
		</label>
		<label style="padding: 5px;margin: 0px; color: #FF0000;display: none" id="userName_errordiv"></label>
		<label style="padding: 5px;margin: 0px;">电话号码：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="phone_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
		</label>
		<label style="padding: 5px;margin: 0px;">QQ：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="qq_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="createUser()" id="craeteUserButton" class="btn btn-primary"><i class="icon-save"></i> Save </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>
	
	<!-- 客户订单信息的查看页面myOrderInfos -->
	<div id="myOrderInfos" class="reveal-modal" style="width: 900px;height: 420px; overflow-x: hidden; overflow-y: hidden">
		<font color="#999999" id="myOrderInfos_customername"></font><br/>
		<table class="table">
			<thead>
				<tr>
					<th>#</th>
					<th>下单时间</th>
					<th>首送时间</th>
					<th>末送时间</th>
					<th>工单次数</th>
					<th>已执行次数</th>
					<th>商品信息</th>
					<th>实付/应付</th>
				</tr>
			</thead>
			<tbody id="myOrderInfosDiv">
				
			</tbody>
		</table>
		<a class="close-reveal-modal">×</a>
	</div>
	
	<!-- 客户订单创建页面myCreateOrderInfos -->
	<div id="craeteOrderByCustomerDiv" class="reveal-modal" style="width: 280px;height: 780px;">
		<input type="hidden" id="customerId_create" name="customerId_create" value="false"/>
		<label style="padding: 5px;margin: 0px;">订货人：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="orderer_craete"  readonly="readonly" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="40"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">购买商品：</label>
		<label style="padding: 5px;margin: 0px;">
			<select id="commodity_craete">
				<option value=""> -- 请选择 -- </option>
				<option value="0"> 混 搭 </option>
				<option value="1"> 单 品 </option>
			</select>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">订单首次执行时间：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="beginTime_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">订单执行次数：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="workNumber_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;" maxlength="20"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">应付金额：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="ablePay_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">实付金额：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="realPay_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">收货人：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="receiver_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">收货人地址：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="address_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">收货人电话：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="receiverPhone_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
			<font style="color:#FF0000"> * </font>
		</label>
		<label style="padding: 5px;margin: 0px;">订单备注：</label>
		<label style="padding: 5px;margin: 0px;">
			<input type="text" id="remark_craete" style="width: 180px ; height: 30px ; padding: 0px ; margin: 0px;"/>
		</label>
		<label style="padding: 5px;margin: 0px;">
			<a href="javascript:void(0);" onclick="createOrderByCustomerId()" id="craeteUserButton" class="btn btn-primary"><i class="icon-save"></i> Save </a>
		</label>
		<a class="close-reveal-modal">×</a>
	</div>
</div>
</body>
</html>