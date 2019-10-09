<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
		<title>${coint.tag }提现记录</title>
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
	//financial.balance
	vip.ajax({
		url : "/admin/financial/balance/tongjiDownload?eIds="+ids+"&isAll="+false+"&maxDetailsId="+maxDetailsId+"&fundType="+fundType,
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
			<th >流水号</th>
               <th >提交时间</th>
               <th >提现地址</th>
               <th >提取个数（个）</th>
               <th >实际个数</th>
               <th >状态</th>
               <th >备注</th>
               <input type="hidden" id="fundType" name="fundType" value="${fundType }"/>
		</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<tbody>
				<tr class="space">
					<td colspan="8">
						<div class="operation" style="padding-left: 18px;">
							<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(false)">统计选中金额</a>
							|
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计全部金额</a>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">提取个数：<font id="totalM"></font></span>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">实际个数：<font id="totalM2"></font></span>
							<input type="hidden" id="maxDetailsId" value="${maxDetailsId }"/>
						</div>
					</td>
				</tr>
			</tbody>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
			<tbody id="row${list.id }" uname="${list.user.userName }" money="<fmt:formatNumber value="${list.amount}" pattern="0.000000##"/>" fee="<fmt:formatNumber value="${list.fees}" pattern="0.000000##"/>" after="<fmt:formatNumber value="${list.afterAmount }" pattern="0.000000##"/><br/>">
					
				<tr class="hd">
					<td colspan="8">
						<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
						<span>提现编号:${list.id} </span>
						<span>提交时间：<fmt:formatDate value="${list.submitTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<span>确认时间：<fmt:formatDate value="${list.manageTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<c:if test="${list.commandId>0 }"><span style="color: red;">自动</span></c:if>
						<c:if test="${list.managerId==1 }"><span style="color: blue;">(免审)</span></c:if>
					</td>
				</tr>								
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_checkbox bl_color" style="vertical-align: middle;">
						<div class="pic_info" style="text-align: left;">
							${list.id }
						</div>
					</td>
					<td class="b_gray">
						<div style="text-align: left;">
							<fmt:formatDate value="${list.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
						</div>
					</td>
					<td class="b_gray">
						<div style="text-align: left;">
							<a style="color: #006699;" href="${coint.web }${list.toAddress }" target="_blank">${list.toAddress }</a>
						</div>
					</td>
					<td class="b_gray">
						<div style="text-align: right;">
					    	<fmt:formatNumber value="${list.amount}" pattern="0.000000##"/><br/>
					    </div>
					</td>
					<td class="b_gray">
						<div style="text-align: right;">
					    <font style="color:red; font-size: 14px;font-weight: bold;">
					   		<fmt:formatNumber value="${list.afterAmount }" pattern="0.000000##"/><br/>
						</font>
						</div>
					</td>
					<td class="b_gray">
						<div style="text-align: left;">
							${list.showStat }
						</div>
					</td>
					<td class="b_gray">
						<div style="text-align: left;">
							${list.remark }
						</div>
					</td>
					
				</tr>
			</tbody>
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="8">
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
					<td colspan="8">
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