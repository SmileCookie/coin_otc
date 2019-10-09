<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>账户快照</title>
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
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContaint">
					<div class="formline">
						<span style="float:left;" class="formtit">快照来源：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="system" id="system" style="width:150px;display: none;" selectid="select_24962646">
					           	<option value="0">管理员创建</option>
				             	<option value="1">定时器创建</option>
					         </select>
					         <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span style="float:left;" class="formtit">账户类型：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="fundType" id="fundType" style="width:140px;display: none;" selectid="select_24962645">
				             	<option value="1">人民币账户</option>
						     	<option value="2">比特币账户</option>
						        <option value="3">莱特币账户</option>
					         </select>
					         <div class="SelectGray" id="select_24962645"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">快照时间：</span> 
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="startDate" name="startDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;"/>
						</span> 
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
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
	vip.list.funcName = "账户快照";
	vip.list.basePath = "/admin/financial/chart/";
	
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
</script>

</body>
</html>
