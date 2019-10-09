<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户</title>
  <jsp:include page="/admins/top.jsp" />
<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
.infunds{color: #0088CC;font-size: 14px;font-weight: bold;}
.outfunds{color: #B94A48;font-size: 14px;font-weight: bold;}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					
					<div class="formline">
						<span class="formtit">客户端类型：</span> 
						<span class="formcon">
							<select id="type" name="type">
								<option value="">-请选择-</option>
								<option value="android">android</option>
								<option value="ios">ios</option>
								<%--<option value="越狱ios">越狱ios</option>--%>
								<%--<option value="windows">windows</option>--%>
							</select>
						</span>&nbsp;
						
						<span class="formtit">版本名称：</span>
						<span class="formcon">
							<input mytitle="字段要求填写一个长度小于50的字符串" name="name" pattern="limit(0,50)" size="20" type="text"/>
						</span>&nbsp;
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 500});">添加</a>
						</p>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
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
	vip.list.funcName = "";
	vip.list.basePath = "/admin/app/";
// 	document.domain = "${baseDomain}";
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

function changeKey(ids){
	if(!couldPass){
		commids = ids;
		googleCode("changeKey", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "此操作会更改APP所有版本授权，请谨慎操作，如有疑问，请咨询移动开发组成员，确定要changeAppKey吗？",
		url : "/admin/app/changeAppKey?id="+commids+"&mCode="+ids
	});
}

</script>

</body>
</html>