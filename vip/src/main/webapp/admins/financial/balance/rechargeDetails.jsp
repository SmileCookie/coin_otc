<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>${coint.tag }充值记录</title>
   <jsp:include page="/admins/top.jsp" />
		<style type="text/css">
		label.checkbox{  margin: 3px 6px 0 7px;}
		label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
			.pic_info .pic{width:30px;height:30px;}
			body{background: #FFFFFF;}
			.item_list_bgcolor{background: #FFFFFF;}
			.form-search span{line-height: 30px;}
			.tb-list2 th, .tb-list2 td{padding: 2px 10px;}
			.form-search{padding:3px 0 3px 10px;}
		</style>
		<script type="text/javascript" src="${static_domain }/statics/js/admin/admin.js"></script>
<script type="text/javascript">
function tongji(isAll){
	//alert("hello.tongji.jsp");
	var maxDetailsId = $("#maxDetailsId").val();
	var fundType = $("#fundType").val();
	//alert("maxDetailsId = " + maxDetailsId);
	var ids="";
	if(!isAll){
		$(".checkItem").each(function(){
			var id=$(this).val();
			if($(this).attr("checked")==true){
				ids+=id+",";
			}
		});
		var list=ids.split(",");
		if(list.length==1){ 
			Wrong("请选择一项"); 
			return;
		}
	}
	//alert("hello.tongji.jsp2");
	//alert(ids + "," + isAll);
	vip.ajax({
		url : "/admin/financial/balance/tongjiDetail?eIds="+ids+"&isAll="+isAll+"&maxDetailsId="+maxDetailsId+"&fundType="+fundType,
		dataType : "json",
		suc : function(json) {
			var obj = json.datas;
			$.each(json.datas, function(i,v){
				if(i==0){
					$("#totalM").text(v).parent("span").show();
				}else{
					$("#totalM"+(i+1)).text(v).parent("span").show();
				}
			});
		}
	});
}

//全选按钮的方法
function selectAll(){
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
}
		</script>
	</head>
	<body> 
		<div class="mains">
		
			<div class="col-main">
				<div class="tab-body" id="shopslist">
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th></th>
		<th>用户信息</th>
        <th>交易类型</th>
        <th>金额（${coint.propTag }）</th>
        <th>备注</th>
        <th>状态</th>
        <input type="hidden" id="fundType" name="fundType" value="${fundType }"/>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:if test="${logAdmin.rid==1 || logAdmin.rid==6}">
				<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
				<a class="AButton yellow_button manyJisuan" href="javascript:tongji(false)">统计选中金额</a>
				|
				<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计全部金额</a>
				<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">总额：<font id="totalM"></font></span>
				
				<input type="hidden" id="maxDetailsId" value="${maxDetailsId }"/>
			</c:if>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
			<tbody id="row${list.detailsId }" uname="${list.userName }" money="${list.amount }">
				<tr class="hd">
					<td colspan="7">
						<input type="checkbox" style="display:none;" value="${list.detailsId}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
						<span>充值编号:${list.detailsId} </span><span>充值时间：<fmt:formatDate value="${list.sendTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<span>确认时间：<fmt:formatDate value="${list.configTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<c:if test="${list.fromAddr != null }">从<a style="color: #006699;" href="${coint.web }${list.fromAddr }" target="_blank">${list.fromAddr }</a></c:if>
						<c:if test="${list.toAddr != null }">到<a style="color: #006699;" href="${coint.web }${list.toAddr }" target="_blank">${list.toAddr }</a></c:if>
						<c:if test="${list.adminId > 0 }"><span>管理员：【${list.aUser.id},${list.aUser.admName }】 </span></c:if>
					</td>
				</tr>								
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_checkbox bl_color" style="vertical-align: middle;">
					</td>
					<td class="commodity_info">
						<div class="pic_info" style="text-align: left;">
							${list.userId}<br>
							<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>
						</div>
					</td>
					<td class="b_gray">
						<div style="text-align: left;">
							${list.inType }
						</div>
					</td>
					<td class="b_gray" style="font-weight: bold;color: #F37800;">
						<div style="text-align: right;">
							<fmt:formatNumber value="${list.amount }" pattern="0.00######"/><br/>
						</div>
					</td>
					<td>
						<div style="text-align: left;">
							${list.remark }${list.type==1?('（确认次数：'.concat(list.confirmTimes).concat('）')):'' }
						</div>
					</td>
					<td>
						<div style="text-align: left;">
							${list.showStatu }
						</div>
					</td>
					
				</tr>
			</tbody>
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="7">
						<div id="page_navA" class="page_nav">
							<div class="con">
								<c:if test="${pager!=null}">${pager}</c:if>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</c:when>
		<c:otherwise>
			<tbody class="air-tips">
				<tr>
					<td colspan="7">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
	
</table>
				</div>
			</div>
		</div>
</body>
</html>
