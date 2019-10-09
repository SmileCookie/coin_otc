<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>免审额度申请</title>
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
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<input type="hidden" name="${coint.coinParam }" value="${coint.tag }"  />
				<div class="formline">
					<span class="formtit">用户名：</span> 
					<span class="formcon">
						<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
					</span>

					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					</p>
				</div>
	
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'wait'})" class="current" id="wait"><span>等待审核</span></a>
			<a href="javascript:vip.list.search({tab:'pass'})" id="pass"><span>审核通过</span></a>
			<a href="javascript:vip.list.search({tab:'unpass'})" id="unpass"><span>审核不通过</span></a>
			<a href="javascript:vip.list.search({tab:'nosub'})" id="nosub"><span>不需审核</span></a>
			<a href="javascript:vip.list.search({tab:'all'})" id="all"><span>所有记录</span></a>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "额度申请";
	vip.list.basePath = "/admin/btc/freecash/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function agree(id){
	Iframe({
		Url : "/admin/btc/freecash/edit?id=" + id+"&coint=${coint.stag}",
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 330,
        isShowIframeTitle: true,
		Title : "编辑审核"
	});	  
}

function reason(id){
	Iframe({
		Url : "/admin/btc/freecash/reason?id=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "不通过"
	});	  
}

function unpass(id,reason,ope){
	var ajaxUrl = "/admin/btc/freecash/unpass";
	
	vip.ajax({
		url : ajaxUrl+"?vid=" + id+"&reason="+encodeURIComponent(reason)+"&coint=${coint.stag}",
		dataType : "json",
		suc : function(json) {
			Right(json.des, {callback:"reload2()"});
		}
	});
}

//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function passmore(){
	var ids="";
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

	Ask2({Msg:"确定要通过选中的项吗？", call:function(){
		vip.ajax({
			url : "/admin/btc/freecash/passMore?&vids="+ids+"&coint=${coint.stag}",
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}
</script>

</body>
</html>
