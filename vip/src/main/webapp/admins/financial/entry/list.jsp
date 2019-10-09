<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>账务录入</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}
.form-search .formline{float:left;}
.form-search p{float:none;}
.operation { height: 20px; line-height: 20px; text-align: left;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{width:55px;}

.form-search .formline{width: 98%;}

label.checkbox {
    margin: 3px 6px 0 7px;
}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint" >
				<div class="form-search" id="searchContaint">
					<div class="formline">
						<span class="formtit">编号：</span> 
						<span style="float:left;margin: 1px 3px 0 0px;" class="formcon">
							<select name="orderId" id="orderId" style="width:80px;display: none;" selectid="select_47460064">
					           <option value="0">等于</option>
					           <option value="1">大于等于</option>
					           <option value="-1">小于等于</option>
					         </select>
					         <div class="SelectGray" id="select_47460064"><span><i style="width: 31px;">等于</i></span></div>
						</span>
						<span class="formcon">
							<input id="eid" mytitle="编号要求填写一个长度小于20的字符串" name="eid" pattern="limit(0,50)" size="10" type="text"/>
						</span>
					
						<span style="float:left;" class="formtit">账户选择：</span> 
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="accountId" id="accountId" style="width:200px;display: none;" selectid="select_24962646">
					           <option value="">全部</option>
					           <c:forEach var="account" items="${accounts}">
					             	<option value="${account.id}">${account.name}</option>
				           		</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span style="float:left;" class="formtit">用途选择：</span> 
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="useTypeId" id="useTypeId" style="width:180px;display: none;" selectid="select_24962645">
					           <option value="">全部</option>
					           <c:forEach var="useType" items="${useTypes}">
					             	<option value="${useType.id}">${useType.name}</option>
				           		</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962645"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<c:if test="${logAdmin.rid==1 || logAdmin.rid==6}">
						<span style="float:left;" class="formtit">管理员：</span> 
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="adminId" id="adminId" style="width:120px;display: none;" selectid="select_24962655">
					           <option value="">全部</option>
					           <c:forEach var="adm" items="${admins}">
					             	<option value="${adm.id}">${adm.admName}</option>
				           		</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962655"><span><i style="width: 111px;">全部</i></span></div>
						</span>
                        </c:if>
						
					</div>
					<div class="formline">
						<span class="formtit">时间：</span> 
						<span class="formcon">
							<select name="dateTime" id="dateTime" selectid="select_2053715" style="display: none;width:100px;">
								<option value="">全部</option>
								<option value="1">今天</option>
								<option value="3">近三天内</option>
								<option value="7">本周内</option>
								<option value="30">本月内</option>
<!-- 								<option value="60">上个月</option> -->
								<option value="5">任意时间范围</option>
							</select>
							<div class="SelectGray" id="select_2053715"><span><i style="width: 100px;">全部</i></span></div>
						</span>
						
						<p id="anytimeDiv" style="display:none;">
							<span class="spacing">从</span>
							<span class="formcon mr_5">
								<input type="text" class="inputW2 Wdate" id="startDate" name="startDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
							</span> 
							<span class="spacing">到</span> 
							<span class="formcon">
								<input type="text" class="inputW2 Wdate" id="endDate" name="endDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
							</span>
						</p>
						
						<span class="formtit">用户ID：</span>
						<span class="formcon">
							<input id="userId" mytitle="用户id要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span class="formtit">备注：</span> 
						<span class="formcon">
							<input id="memo" mytitle="要求填写一个长度小于50的字符串" name="memo" pattern="limit(0,50)" size="20" type="text"/>
						</span>
					
					</div>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<%--<a id="" class="search-submit" href="javascript:vip.list.aoru({id:0,height:580});">添加</a>--%>

						<a id="" class="search-submit" href="#">添加</a>
						<a class="search-submit" href="javascript:exportUser('');">导出excel</a>
					</p>
	
				</div>
		
			</form>
		</div>
		
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "账务录入";
	vip.list.basePath = "/admin/financial/entry/";
	
	$(".item_list").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
    
    $("#dateTime").change(function(){
		if($(this).val()=="5"){
			$("#anytimeDiv").slideDown("fast");
		}else{
			$("#anytimeDiv").hide();
		}
	});
});

function reload2(){
	Close();
	vip.list.reload();
}

//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function tongji(isAll){
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
	
	vip.ajax({
		url : "/admin/financial/entry/tongji?eIds=" + ids+"&isAll="+isAll,
		dataType : "json",
		suc : function(json) {
			var obj = json.datas;
			$("#inMoney").text(obj.inTotalMoney).parent("span").show();
			$("#outMoney").text(obj.outTotalMoney).parent("span").show();
		}
	});
}

function print(){
	//
	var ajaxAA = $("#searchContaint input[name='ajaxAA']");
	if(ajaxAA.length <= 0){
		$("#searchContaint").append('<input name="ajaxAA" type="hidden"/>');
	}
	ajaxAA = $("#searchContaint input[name='ajaxAA']");
	ajaxAA.val($("#shopslist").html());
	document.searchForm.action="/admin/print"; 
	document.searchForm.method = "post";
	document.searchForm.target = "_blank";
	document.searchForm.submit();
}
</script>
<script type="text/javascript">
function exportUser(mCode){
	if(!couldPass){
		googleCode("exportUser", false);
		return;
	}
	couldPass = false;
	Close();
	var actionUrl = "/admin/financial/entry/exportUser?mCode="+mCode;
	var datas = FormToStr("searchContaint");
	location.href = actionUrl+"&"+datas;
}
</script>
</body>
</html>
