<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>IP黑名单管理</title>
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
						<span class="formtit">证件号码：</span>
						<span class="formcon">
							<input id="cardNo" name="cardNo" size="20" type="text"/>
						</span>

						<p>
							<a class="search-submit" href="javascript:vip.list.search();">查询</a>
							<a class="search-submit" id="idSearch" href="javascript:aoru('');">添加</a>
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
	vip.list.basePath = "/admin/user/authen/idcardBlackListAction/";
	document.domain = "${baseDomain}";
});

function reload2(){
	Close();
	vip.list.reload();
}

function aoru(id){
	Iframe({
		Url : "/admin/user/authen/idcardBlackListAction/aoru?id="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 500,
		Height: 280,
        isShowIframeTitle: true,
		Title : "添加证件号码黑名单"
	});	  
}

var inAjaxing = false;
function ajaxUrl(url, dataType){
	if(inAjaxing)
		return;

	inAjaxing = true;
	$.ajax( {
		async : true,
		cache : true,
		type : "POST",
		dataType : dataType,
		data : "",
		url : url,
		error : function(xml) {
			inAjaxing = false;
		},
		timeout : 60000,
		success : function(xml) {
			inAjaxing = false;
			if ($(xml).find("State").text() == "true") {
				Right($(xml).find("MainData").text(), {callback : "reload2()"});
			} else{
				Wrong($(xml).find("Des").text());
			}
		}
	});
}

//全选按钮的方法
function selectAll(){
	changeCheckBox('delAll');
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

</script>

</body>
</html>
