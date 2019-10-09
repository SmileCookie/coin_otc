<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>异常记录</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}
.form-search .formline{float:left;}
.form-search p{float:none;}
.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContaint">
					<div class="formline">
						<span style="float:left;" class="formtit">账户选择：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="accountId" id="accountId" style="width:200px;display: none;" selectid="select_24962646">
					           <option value="">全部</option>
					           <c:forEach var="account" items="${accounts}">
					             	<option value="${account.id}">${account.name}</option>
				           		</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">时间：</span> 
						<span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="startDate" name="startDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span> 
						<span class="spacing">到</span> 
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="endDate" name="endDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
						<span class="formtit">用户名：</span> 
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span class="formtit">备注：</span> 
						<span class="formcon">
							<input id="memo" mytitle="要求填写一个长度小于50的字符串" name="memo" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span style="float:left;" class="formtit">状态：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="status" id="status" style="width:100px;display: none;" selectid="">
					           <option value="">全部</option>
				             	<option value="0">未处理</option>
				             	<option value="1">已处理</option>
				             	<option value="2">已取消</option>
					         </select>
						</span>
					
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a id="" class="search-submit" href="javascript:vip.list.aoru({id:0,height:420});">添加</a>
						</p>
					</div>
	
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
	vip.list.funcName = "异常记录";
	vip.list.basePath = "/admin/financial/errcord/";
	
	$(".item_list").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
});

function reload2(){
	Close();
	vip.list.reload();
}

function deal(id) {
	Iframe({
		Url : '/admin/financial/errcord/deal?id=' + id,
		Width : 550,
		Height : 380,
        isShowIframeTitle: true,
		Title : "处理异常信息"
	});
}

function cancel(id){
	var tit = "您确认要取消该记录吗？";
	Ask2({
		Msg : tit,
		call : function(){
			vip.ajax({url : "/admin/financial/errcord/cancel?id="+id , suc : function(xml){
				Right($(xml).find("MainData").text() , {call : function(){
					reload2();
				}});
			}}); 
		}
	});
}
</script>

</body>
</html>
